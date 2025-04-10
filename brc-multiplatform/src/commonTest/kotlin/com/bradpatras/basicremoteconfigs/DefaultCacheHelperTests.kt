package com.bradpatras.basicremoteconfigs

import com.bradpatras.basicremoteconfigs.cache.DefaultCacheHelper
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant.Companion.fromEpochSeconds
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FakeClock : Clock {
    private var time = fromEpochSeconds(1743134025L)

    override fun now() = time
}

class CacheHelperTest {
    @Test
    fun testSetCacheConfigs() = runTest {
        val fileSystem = FakeFileSystem()
        val configsPath = "/path/to/configs.json".toPath()
        val cacheHelper = DefaultCacheHelper(configsPath, fileSystem)

        val configs = JsonObject(
            mapOf(
                "ver" to JsonPrimitive("1"),
                "myString" to JsonPrimitive("Hello there"),
                "myInt" to JsonPrimitive(100),
                "myBoolean" to JsonPrimitive(true),
                "myList" to JsonArray(listOf(JsonPrimitive("one"), JsonPrimitive("two")))
            )
        )

        cacheHelper.setCacheConfigs(configs)
        fileSystem.checkNoOpenFiles()
        assertTrue(fileSystem.exists(configsPath))
    }

    @Test
    fun testGetCacheConfigs() = runTest {
        val fileSystem = FakeFileSystem()
        val configsPath = "/path/to/configs.json".toPath()
        val cacheHelper = DefaultCacheHelper(configsPath, fileSystem)

        val configs = JsonObject(
            mapOf(
                "ver" to JsonPrimitive("1"),
                "myString" to JsonPrimitive("Hello there"),
                "myInt" to JsonPrimitive(100),
                "myBoolean" to JsonPrimitive(true),
                "myList" to JsonArray(listOf(JsonPrimitive("one"), JsonPrimitive("two")))
            )
        )

        cacheHelper.setCacheConfigs(configs)
        val fetchedConfigs = cacheHelper.getCacheConfigs()

        assertEquals(fetchedConfigs, configs)
        fileSystem.checkNoOpenFiles()
    }

    @Test
    fun testDeleteCacheNoConfigs() {
        val fileSystem = FakeFileSystem()
        val configsPath = "/path/to/configs.json".toPath()
        val cacheHelper = DefaultCacheHelper(configsPath, fileSystem)

        // Calling delete on non-existent cache file should not raise any exceptions
        cacheHelper.deleteCacheFile()
    }

    @Test
    fun testGetCacheConfigsNoCache() = runTest {
        val fileSystem = FakeFileSystem()
        val configsPath = "/path/to/configs.json".toPath()
        val cacheHelper = DefaultCacheHelper(configsPath, fileSystem)

        assertNull(cacheHelper.getCacheConfigs())
    }

    @Test
    fun testDeleteCache() = runTest {
        val fileSystem = FakeFileSystem()
        val configsPath = "/path/to/configs.json".toPath()
        val cacheHelper = DefaultCacheHelper(configsPath, fileSystem)

        val configs = JsonObject(
            mapOf(
                "ver" to JsonPrimitive("1"),
                "myString" to JsonPrimitive("Hello there"),
                "myInt" to JsonPrimitive(100),
                "myBoolean" to JsonPrimitive(true),
                "myList" to JsonArray(
                    listOf(
                        JsonPrimitive("one"),
                        JsonPrimitive("two")
                    )
                )
            )
        )

        cacheHelper.setCacheConfigs(configs)

        cacheHelper.deleteCacheFile()
        fileSystem.checkNoOpenFiles()
        assertFalse(fileSystem.exists(configsPath))
    }

    @Test
    fun testGetLastModified() = runTest {
        val clock = FakeClock()
        val fileSystem = FakeFileSystem(clock)
        val configsPath = "/path/to/configs.json".toPath()
        val cacheHelper = DefaultCacheHelper(configsPath, fileSystem)
        val configs = JsonObject(
            mapOf(
                "ver" to JsonPrimitive("1")
            )
        )

        cacheHelper.setCacheConfigs(configs)

        assertEquals(cacheHelper.getLastModified(), clock.now())
    }

    @Test
    fun testGetLastModifiedNoCache() {
        val fileSystem = FakeFileSystem()
        val configsPath = "/path/to/configs.json".toPath()
        val cacheHelper = DefaultCacheHelper(configsPath, fileSystem)

        assertNull(cacheHelper.getLastModified())
    }
}