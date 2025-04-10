package com.bradpatras.basicremoteconfigs

import com.bradpatras.basicremoteconfigs.cache.CacheHelper
import com.bradpatras.basicremoteconfigs.cache.InstantProvider
import kotlinx.datetime.Instant
import kotlinx.serialization.json.JsonObject

class FakeCacheHelper(
    private var configs: JsonObject? = null,
    private var lastModified: Instant? = null,
    private val instantProvider: InstantProvider
): CacheHelper {
    override suspend fun getCacheConfigs(): JsonObject? = configs

    override suspend fun setCacheConfigs(configs: JsonObject) {
        this.configs = configs
        this.lastModified = instantProvider.now()
    }

    override fun getLastModified(): Instant? = lastModified

    override fun deleteCacheFile() {
        this.configs = null
        this.lastModified = null
    }
}