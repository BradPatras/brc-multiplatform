package io.github.bradpatras.basicremoteconfigs.cache

import kotlinx.datetime.Instant
import kotlinx.serialization.json.JsonObject

internal interface CacheHelper {
    suspend fun getCacheConfigs(): JsonObject?

    suspend fun setCacheConfigs(configs: JsonObject)

    fun getLastModified(): Instant?

    fun deleteCacheFile()
}