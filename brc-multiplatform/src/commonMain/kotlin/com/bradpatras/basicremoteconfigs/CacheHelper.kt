package com.bradpatras.basicremoteconfigs

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.SYSTEM
import okio.buffer
import okio.use

internal class CacheHelper(private val cachePath: Path, private val fileSystem: FileSystem) {
//    suspend fun getCacheConfigs(): JsonObject? = withContext(Dispatchers.IO) {
//        val cacheFile = getCacheFile()
//
//        // Return early if cache file doesn't exist
//        if (!cacheFile.exists()) return@withContext null
//
//        try {
//            val fileString = FileInputStream(cacheFile).use {
//                return@use it.readBytes().decodeToString()
//            }
//
//            return@withContext JsonObject(fileString)
//        } catch (exception: Throwable) {
//            throw exception
//        }
//    }

    suspend fun getCacheConfigs(): JsonObject? = withContext(Dispatchers.IO) {
        // return early if cache file doesn't exist
        if (!fileSystem.exists(cachePath)) return@withContext null

        fileSystem.source(cachePath).use { fileSource ->
            fileSource.buffer().use { bufferedFileSource ->
                val element = Json.Default.parseToJsonElement(bufferedFileSource.readUtf8())
                return@withContext element.jsonObject
            }
        }
    }

    suspend fun setCacheConfigs(configsJson: JSONObject): Unit = withContext(Dispatchers.IO) {
        val cacheFile = getCacheFile()

        // Create if cache file doesn't exist
        if (!cacheFile.exists()) {
            cacheFile.createNewFile()
        }

        try {
            FileOutputStream(cacheFile).use {
                it.write(configsJson.toString().encodeToByteArray())
            }
        } catch (exception: Throwable) {
            throw exception
        }
    }

    fun getLastModified(): Long? {
        return if (getCacheFile().exists()) {
            getCacheFile().lastModified()
        } else {
            null
        }
    }

    fun deleteCacheFile() {
        val cacheFile = getCacheFile()
        cacheFile.delete()
    }

    private fun getCacheFile(): File = File(BrcInitializer.filesDirectory, cacheFilename)
}