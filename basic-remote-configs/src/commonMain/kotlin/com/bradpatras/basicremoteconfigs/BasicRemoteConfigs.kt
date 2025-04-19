package com.bradpatras.basicremoteconfigs

import com.bradpatras.basicremoteconfigs.cache.CacheHelper
import com.bradpatras.basicremoteconfigs.cache.DefaultCacheHelper
import com.bradpatras.basicremoteconfigs.cache.DefaultInstantProvider
import com.bradpatras.basicremoteconfigs.cache.InstantProvider
import com.bradpatras.basicremoteconfigs.network.DefaultNetworkHelper
import com.bradpatras.basicremoteconfigs.network.NetworkHelper
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.Instant
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM
import kotlin.time.Duration

// Version code representing an unknown or un-fetched version
private const val VERSION_NONE = -1

// Json key for the config version
private const val VERSION_KEY = "v"

// Cache filename
private const val CONFIG_CACHE_FILENAME = "brc_cache"

// Amount of hours the cached configs remain valid
private val CACHE_EXPIRATION: Duration = Duration.parse("24h")

@Suppress("MemberVisibilityCanBePrivate", "unused")
public class BasicRemoteConfigs internal constructor(
    private val remoteUrl: String,
    private val customHeaders: HashMap<String, String> = HashMap(),
    private val instantProvider: InstantProvider,
    private val cacheHelper: CacheHelper,
    private val networkHelper: NetworkHelper
) {
    private var _values: JsonObject = JsonObject(emptyMap())

    /**
     * Basic remote configs
     *
     * @property remoteUrl Path pointing to the remote server providing the configs
     * @property customHeaders Additional headers that will be sent with the fetch request
     * @constructor Create empty Basic remote configs
     */
    public constructor(
        remoteUrl: String,
        customHeaders: HashMap<String, String> = HashMap()
    ) : this(
        remoteUrl = remoteUrl,
        customHeaders = customHeaders,
        instantProvider = DefaultInstantProvider(),
        cacheHelper = DefaultCacheHelper(CONFIG_CACHE_FILENAME.toPath(), FileSystem.SYSTEM),
        networkHelper = DefaultNetworkHelper()
    )

    /**
     * Hash map containing the current config values
     */
    val values: JsonObject get() = _values

    /**
     * Version parsed from the fetched configs.  If configs haven't been fetched or
     * there was no version key included, the version will default to *-1*
     */
    val version: Int get() = values[VERSION_KEY]?.jsonPrimitive?.intOrNull ?: VERSION_NONE

    /**
     * An Instant representing the last time the configs were successfully fetched and updated.
     */
    var fetchDate: Instant? = null

    /**
     * Fetch configs, either from cache or remote server. Cached configs will be used if they
     * exist, the expiration date hasn't been reached and the `ignoreCache` flag is false or
     * omitted.
     * @param ignoreCache If true, the cache will be ignored and configs will be fetched from
     * the remote server.
     */
    suspend fun fetchConfigs(ignoreCache: Boolean = false): Unit = coroutineScope {
        val cacheConfigs = cacheHelper.getCacheConfigs()
        val cacheLastModified = cacheHelper.getLastModified() ?: Instant.DISTANT_PAST
        val cacheIsNotExpired = instantProvider.now() - cacheLastModified < CACHE_EXPIRATION

        if (!ignoreCache && cacheConfigs != null && cacheIsNotExpired) {
            _values = cacheConfigs
        } else {
            try {
                fetchRemoteConfigs()
            } catch (e: Throwable) {
                _values = cacheConfigs ?: JsonObject(emptyMap())
            }
        }
    }

    /**
     * Delete the locally stored config file
     *
     */
    fun clearCache() {
        cacheHelper.deleteCacheFile()
        _values = JsonObject(emptyMap())
        fetchDate = null
    }

    private suspend fun fetchRemoteConfigs(): Unit = coroutineScope {
        try {
            val configs = requireNotNull(networkHelper.requestJson(remoteUrl, customHeaders))
            val newVersion = configs[VERSION_KEY]?.jsonPrimitive?.intOrNull ?: VERSION_NONE

            // if version hasn't changed, do nothing
            if ((newVersion != version) or (newVersion == VERSION_NONE)) {
                fetchDate = instantProvider.now()
                cacheHelper.setCacheConfigs(configs)
                _values = configs
            }
        } catch (e: Throwable) {
            throw e
        }
    }

    /**
     * Get keys of all current configs
     *
     * @return Set containing config keys
     */
    fun getKeys(): Set<String> {
        return values.keys
    }

    /**
     * Get boolean
     *
     * @param key
     * @return Boolean value associated with key, null if key or value doesn't exist.
     */
    fun getBoolean(key: String): Boolean? {
        return values[key] as? Boolean?
    }

    /**
     * Get int
     *
     * @param key
     * @return Int value associated with key, null if key or value doesn't exist.
     */
    fun getInt(key: String): Int? {
        return values[key]?.jsonPrimitive?.intOrNull
    }

    /**
     * Get string
     *
     * @param key
     * @return String value associated with key, null if key or value doesn't exist.
     */
    fun getString(key: String): String? {
        return values[key]?.jsonPrimitive?.takeIf { it.isString }?.content
    }

    /**
     * Get boolean array
     *
     * @param key
     * @return Boolean array associated with key, null if key or value doesn't exist.
     */
    fun getBooleanArray(key: String): Array<Boolean>? {
        return values[key]?.jsonArray?.mapNotNull {
            it.jsonPrimitive.booleanOrNull
        }?.toTypedArray() ?: return null
    }

    /**
     * Get int array
     *
     * @param key
     * @return Int array associated with key, null if key or value doesn't exist.
     */
    fun getIntArray(key: String): Array<Int>? {
        return values[key]?.jsonArray?.mapNotNull {
            it.jsonPrimitive.intOrNull
        }?.toTypedArray() ?: return null
    }

    /**
     * Get string array
     *
     * @param key
     * @return String array associated with key, null if key or value doesn't exist.
     */
    fun getStringArray(key: String): Array<String>? {
        return values[key]?.jsonArray?.mapNotNull { element ->
            element.jsonPrimitive.takeIf { it.isString }?.content
        }?.toTypedArray() ?: return null
    }
}