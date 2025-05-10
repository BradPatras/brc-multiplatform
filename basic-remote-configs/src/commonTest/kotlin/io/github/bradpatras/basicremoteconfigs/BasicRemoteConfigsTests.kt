package io.github.bradpatras.basicremoteconfigs

import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

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
        assertEquals(brc.getString("test"), "network")
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
        assertEquals(brc.getString("test"), "cache")
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
        assertEquals(brc.getString("test"), "network")
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
        assertEquals(brc.getKeys(), emptySet())
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

        try {
            brc.fetchConfigs(ignoreCache = true)
        } catch (e: Throwable) {
            assertEquals(brc.getString("test"), "cache")
        }
    }

    @Test
    fun testNetworkException() = runTest {
        val cacheHelper = FakeCacheHelper(
            null,
            lastModified = Instant.DISTANT_PAST,
            instantProvider = FakeInstantProvider(Instant.DISTANT_FUTURE)
        )
        val networkHelper = FakeThrowingNetworkHelper()

        val brc = BasicRemoteConfigs(
            remoteUrl = "http://google.com/",
            instantProvider = FakeInstantProvider(Instant.DISTANT_FUTURE),
            cacheHelper = cacheHelper,
            networkHelper = networkHelper
        )

        assertFails {
            brc.fetchConfigs(ignoreCache = true)
        }
    }
}