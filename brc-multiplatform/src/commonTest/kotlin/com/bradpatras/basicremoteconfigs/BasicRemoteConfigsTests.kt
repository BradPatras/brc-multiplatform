package com.bradpatras.basicremoteconfigs

import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BasicRemoteConfigsTests {
    @Test
    fun testFetchExpiredCache() = runTest {
        val networkConfigs = buildJsonObject {
            put("test", "network")
        }.jsonObject
        val cacheConfigs = buildJsonObject {
            put("test", "cache")
        }.jsonObject

        val cacheHelper = FakeCacheHelper(
            cacheConfigs,
            lastModified = Instant.DISTANT_PAST,
            instantProvider = FakeInstantProvider(Instant.DISTANT_FUTURE)
        )
        val networkHelper = FakeNetworkHelper(
            networkConfigs
        )

        val brc = BasicRemoteConfigs(
            remoteUrl = "http://google.com/",
            instantProvider = FakeInstantProvider(Instant.DISTANT_FUTURE),
            cacheHelper = cacheHelper,
            networkHelper = networkHelper
        )

        brc.fetchConfigs(ignoreCache = false)
        assertEquals(brc.values, networkConfigs)
    }

    @Test
    fun testFetchNonExpiredCache() = runTest {
        // should fetch from cache since it's not expired
        val networkConfigs = buildJsonObject {
            put("test", "network")
        }.jsonObject
        val cacheConfigs = buildJsonObject {
            put("test", "cache")
        }.jsonObject

        val cacheHelper = FakeCacheHelper(
            cacheConfigs,
            lastModified = Instant.DISTANT_FUTURE,
            instantProvider = FakeInstantProvider(Instant.DISTANT_FUTURE)
        )
        val networkHelper = FakeNetworkHelper(
            networkConfigs
        )

        val brc = BasicRemoteConfigs(
            remoteUrl = "http://google.com/",
            instantProvider = FakeInstantProvider(Instant.DISTANT_FUTURE),
            cacheHelper = cacheHelper,
            networkHelper = networkHelper
        )

        brc.fetchConfigs(ignoreCache = false)
        assertEquals(brc.values, cacheConfigs)
    }

    @Test
    fun testFetchIgnoreCache() = runTest {
        val networkConfigs = buildJsonObject {
            put("test", "network")
        }.jsonObject
        val cacheConfigs = buildJsonObject {
            put("test", "cache")
        }.jsonObject

        val cacheHelper = FakeCacheHelper(
            cacheConfigs,
            lastModified = Instant.DISTANT_FUTURE,
            instantProvider = FakeInstantProvider(Instant.DISTANT_FUTURE)
        )
        val networkHelper = FakeNetworkHelper(
            networkConfigs
        )

        val brc = BasicRemoteConfigs(
            remoteUrl = "http://google.com/",
            instantProvider = FakeInstantProvider(Instant.DISTANT_FUTURE),
            cacheHelper = cacheHelper,
            networkHelper = networkHelper
        )

        brc.fetchConfigs(ignoreCache = true)
        assertEquals(brc.values, networkConfigs)
    }

    @Test
    fun testClearCache() = runTest {
        // cache should be removed
        val networkConfigs = buildJsonObject {
            put("test", "network")
        }.jsonObject
        val cacheConfigs = buildJsonObject {
            put("test", "cache")
        }.jsonObject

        val cacheHelper = FakeCacheHelper(
            cacheConfigs,
            lastModified = Instant.DISTANT_FUTURE,
            instantProvider = FakeInstantProvider(Instant.DISTANT_FUTURE)
        )
        val networkHelper = FakeNetworkHelper(
            networkConfigs
        )

        val brc = BasicRemoteConfigs(
            remoteUrl = "http://google.com/",
            instantProvider = FakeInstantProvider(Instant.DISTANT_FUTURE),
            cacheHelper = cacheHelper,
            networkHelper = networkHelper
        )

        brc.fetchConfigs()
        brc.clearCache()
        assertEquals(brc.values, JsonObject(emptyMap()))
    }

    @Test
    fun testCacheFallbackOnNetworkFail() = runTest {
        // If network fetch fails, fallback to cache
        val cacheConfigs = buildJsonObject {
            put("test", "cache")
        }.jsonObject

        val cacheHelper = FakeCacheHelper(
            cacheConfigs,
            lastModified = Instant.DISTANT_FUTURE,
            instantProvider = FakeInstantProvider(Instant.DISTANT_FUTURE)
        )
        val networkHelper = FakeThrowingNetworkHelper()

        val brc = BasicRemoteConfigs(
            remoteUrl = "http://google.com/",
            instantProvider = FakeInstantProvider(Instant.DISTANT_FUTURE),
            cacheHelper = cacheHelper,
            networkHelper = networkHelper
        )

        brc.fetchConfigs(ignoreCache = true)
        assertEquals(brc.values, cacheConfigs)
    }
}