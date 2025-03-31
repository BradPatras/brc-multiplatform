package com.bradpatras.basicremoteconfigs

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
private const val VERSION_KEY = "ver"

// Cache filename
private const val CONFIG_CACHE_FILENAME = "brc_cache"

// Amount of hours the cached configs remain valid
private val CACHE_EXPIRATION_HOURS: Duration = Duration.parse("24h")

/**
 * Basic remote configs
 *
 * @property remoteUrl Path pointing to the remote server providing the configs
 * @property customHeaders Additional headers that will be sent with the fetch request
 * @constructor Create empty Basic remote configs
 */
class BasicRemoteConfigs(
    private val remoteUrl: String,
    private val customHeaders: HashMap<String, String> = HashMap(),
    private val instantProvider: InstantProvider = InstantProvider()
) {
    private var _version: Int = VERSION_NONE
    private var _values: JsonObject = JsonObject(emptyMap())
    private val cacheHelper = CacheHelper(CONFIG_CACHE_FILENAME.toPath(), FileSystem.SYSTEM)

    /**
     * Hash map containing the config values
     */
    val values: JsonObject get() = _values

    /**
     * Version parsed from the fetched configs.  If configs haven't been fetched or
     * there was no version key included, the version will default to *-1*
     */
    val version: Int get() = _version

    /**
     * An Instant representing the last time the configs were successfully fetched and updated.
     */
    var fetchDate: Instant? = null

    /**
     * Fetch configs from **url** provided in class constructor.
     * If configs are fetched successfully and contain a **version** value
     * different from what is currently stored, a new value will be emitted
     * from the **valuesFlow** property.
     *
     * **Note:** This function may throw IOException, JSONException and possibly others.
     */
    suspend fun fetchConfigs(ignoreCache: Boolean = false): Unit = coroutineScope {
        val cacheConfigs = cacheHelper.getCacheConfigs()
        val cacheLastModified = cacheHelper.getLastModified() ?: instantProvider.now()
        val cacheExists = cacheConfigs != null
        val cacheIsNotExpired = cacheLastModified - instantProvider.now() < CACHE_EXPIRATION_HOURS

        if (!ignoreCache and cacheExists and cacheIsNotExpired) {
            fetchLocalConfigs()
        } else {
            try {
                fetchRemoteConfigs()
            } catch (e: Throwable) {
                fetchLocalConfigs()
            }
        }
    }

    fun clearCache() {
        cacheHelper.deleteCacheFile()
        _values = JsonObject(emptyMap())
        _version = VERSION_NONE
        fetchDate = null
    }

    private suspend fun fetchRemoteConfigs(): Unit = coroutineScope {
        try {
            val configs = requireNotNull(HttpRequestHelper.makeGetRequest(remoteUrl, customHeaders))
            val newVersion = configs.get(VERSION_KEY)?.jsonPrimitive?.intOrNull ?: VERSION_NONE

            // Do not emit a new value if the version hasn't changed
            if ((newVersion != _version) or (newVersion == VERSION_NONE)) {
                fetchDate = instantProvider.now()
                cacheHelper.setCacheConfigs(configs)
                _values = configs
                _version = newVersion
            }
        } catch (e: Throwable) {
            throw e
        }
    }

    private suspend fun fetchLocalConfigs(): Unit = coroutineScope {
        try {
            val configs = requireNotNull(cacheHelper.getCacheConfigs())
            val newVersion = configs.get(VERSION_KEY)?.jsonPrimitive?.intOrNull ?: VERSION_NONE

            // Do not emit a new value if the version hasn't changed
            if ((newVersion != _version) or (newVersion == VERSION_NONE)) {
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
    suspend fun getStringArray(key: String): Array<String>? {
        return values[key]?.jsonArray?.mapNotNull { element ->
            element.jsonPrimitive.takeIf { it.isString }?.content
        }?.toTypedArray() ?: return null
    }
}