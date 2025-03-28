//package com.bradpatras.basicremoteconfigs
//
//import kotlinx.coroutines.coroutineScope
//import okio.FileSystem
//import okio.Path.Companion.toPath
//import okio.SYSTEM
//import kotlin.collections.HashMap
//
//// Version code representing an unknown or un-fetched version
//private const val VERSION_NONE = -1
//
//// Json key for the config version
//private const val VERSION_KEY = "ver"
//
//// Cache filename
//private const val CONFIG_CACHE_FILENAME = "brc_cache"
//
//// Amount of hours the cached configs remain valid
//private const val CACHE_EXPIRATION_HOURS = 24
//
///**
// * Basic remote configs
// *
// * @property remoteUrl Path pointing to the remote server providing the configs
// * @property customHeaders Additional headers that will be sent with the fetch request
// * @constructor Create empty Basic remote configs
// */
//class BasicRemoteConfigs(
//    private val remoteUrl: String,
//    private val customHeaders: HashMap<String, String> = HashMap()
//) {
//    private var _version: Int = VERSION_NONE
//    private var _values: HashMap<String, Any> = HashMap()
//    private val cacheHelper = CacheHelper(CONFIG_CACHE_FILENAME.toPath(), FileSystem.SYSTEM)
//
//    /**
//     * Hash map containing the config values
//     */
//    val values: HashMap<String, Any> get() = _values
//
//    /**
//     * Version parsed from the fetched configs.  If configs haven't been fetched or
//     * there was no version key included, the version will default to *-1*
//     */
//    val version: Int get() = _version
//
//    /**
//     * A Date representing the last time the configs were successfully fetched and updated.
//     */
//    var fetchDate: Date? = null
//
//    /**
//     * Fetch configs from **url** provided in class constructor.
//     * If configs are fetched successfully and contain a **version** value
//     * different from what is currently stored, a new value will be emitted
//     * from the **valuesFlow** property.
//     *
//     * **Note:** This function may throw IOException, JSONException and possibly others.
//     */
//    suspend fun fetchConfigs(ignoreCache: Boolean = false): Unit = coroutineScope {
//        val cacheConfigs = cacheHelper.getCacheConfigs()
//        val cacheModifiedDate = cacheHelper.getLastModified() ?: DateHelper.now()
//        val cacheExists = cacheConfigs != null
//        val cacheIsNotExpired = DateHelper.hoursSince(cacheModifiedDate) < CACHE_EXPIRATION_HOURS
//
//        if (!ignoreCache and cacheExists and cacheIsNotExpired) {
//            Log.i("BasicRemoteConfigs", "Fetching local configs")
//            fetchLocalConfigs()
//        } else {
//            try {
//                Log.i("BasicRemoteConfigs", "Fetching remote configs")
//                fetchRemoteConfigs()
//            } catch (e: Throwable) {
//                Log.w("BasicRemoteConfigs", "Failed to fetch remote configs. Attempting local cache fallback", e)
//                fetchLocalConfigs()
//            }
//        }
//    }
//
//    fun clearCache() {
//        cacheHelper.deleteCacheFile()
//        _values = HashMap()
//        _version = VERSION_NONE
//        fetchDate = null
//    }
//
//    private suspend fun fetchRemoteConfigs(): Unit = coroutineScope {
//        try {
//            val configs = requireNotNull(HttpRequestHelper.makeGetRequest(remoteUrl, customHeaders))
//            val newVersion = configs[VERSION_KEY] as? Int ?: VERSION_NONE
//            val newValues = configs.toMap()
//
//            // Do not emit a new value if the version hasn't changed
//            if ((newVersion != _version) or (newVersion == VERSION_NONE)) {
//                fetchDate = Date()
//                cacheHelper.setCacheConfigs(configs)
//                _values = newValues
//                _version = newVersion
//            }
//        } catch (e: Throwable) {
//            Log.e("BasicRemoteConfigs", "Failed to parse config json.", e)
//            throw e
//        }
//    }
//
//    private suspend fun fetchLocalConfigs(): Unit = coroutineScope {
//        try {
//            val configs = requireNotNull(cacheHelper.getCacheConfigs())
//            val newVersion = configs[VERSION_KEY] as? Int ?: VERSION_NONE
//            val newValues = configs.toMap()
//
//            // Do not emit a new value if the version hasn't changed
//            if ((newVersion != _version) or (newVersion == VERSION_NONE)) {
//                _values = newValues
//            }
//        } catch (e: Throwable) {
//            Log.e("BasicRemoteConfigs", "Failed to parse config json.", e)
//            throw e
//        }
//    }
//
//    /**
//     * Get keys of all current configs
//     *
//     * @return Set containing config keys
//     */
//    fun getKeys(): Set<String> {
//        return values.keys
//    }
//
//    /**
//     * Get boolean
//     *
//     * @param key
//     * @return Boolean value associated with key, null if key or value doesn't exist.
//     */
//    fun getBoolean(key: String): Boolean? {
//        return values[key] as? Boolean?
//    }
//
//    /**
//     * Get int
//     *
//     * @param key
//     * @return Int value associated with key, null if key or value doesn't exist.
//     */
//    fun getInt(key: String): Int? {
//        return values[key] as? Int
//    }
//
//    /**
//     * Get string
//     *
//     * @param key
//     * @return String value associated with key, null if key or value doesn't exist.
//     */
//    fun getString(key: String): String? {
//        return values[key] as? String
//    }
//
//    /**
//     * Get boolean array
//     *
//     * @param key
//     * @return Boolean array associated with key, null if key or value doesn't exist.
//     */
//    fun getBooleanArray(key: String): Array<Boolean>? {
//        val jsonArray = values[key] as? JSONArray ?: return null
//        val values = mutableListOf<Boolean>()
//        for (i in 0 until jsonArray.length()) {
//            (jsonArray[i] as? Boolean)?.let { values.add(it) }
//        }
//
//        return values.toTypedArray()
//    }
//
//    /**
//     * Get int array
//     *
//     * @param key
//     * @return Int array associated with key, null if key or value doesn't exist.
//     */
//    fun getIntArray(key: String): Array<Int>? {
//        val jsonArray = values[key] as? JSONArray ?: return null
//        val values = mutableListOf<Int>()
//        for (i in 0 until jsonArray.length()) {
//            (jsonArray[i] as? Int)?.let { values.add(it) }
//        }
//
//        return values.toTypedArray()
//    }
//
//    /**
//     * Get string array
//     *
//     * @param key
//     * @return String array associated with key, null if key or value doesn't exist.
//     */
//    suspend fun getStringArray(key: String): Array<String>? {
//        val jsonArray = values[key] as? JSONArray ?: return null
//        val values = mutableListOf<String>()
//        for (i in 0 until jsonArray.length()) {
//            (jsonArray[i] as? String)?.let { values.add(it) }
//        }
//
//        return values.toTypedArray()
//    }
//}
//
//private fun JSONObject.toMap(): HashMap<String, Any> {
//    return HashMap<String, Any>().also { map ->
//        keys().forEach { key ->
//            map[key] = this.get(key)
//        }
//    }
//}