package com.bradpatras.basicremoteconfigs

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.use

internal class CacheHelper(
    private val cachePath: Path,
    private val fileSystem: FileSystem
) {
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

    suspend fun setCacheConfigs(configs: JsonObject) = withContext(Dispatchers.IO) {
        val parent = cachePath.parent ?: Path.DIRECTORY_SEPARATOR.toPath()
        if (!fileSystem.exists(parent)) {
            fileSystem.createDirectories(parent)
        }

        fileSystem.sink(cachePath).use { fileSink ->
            fileSink.buffer().use { bufferedSink ->
                bufferedSink.writeUtf8(Json.encodeToString(JsonObject.serializer(), configs))
            }
        }
    }

    fun getLastModified(): Instant? {
        return if (fileSystem.exists(cachePath)) {
            fileSystem.metadata(cachePath).lastModifiedAtMillis?.let {
                Instant.fromEpochMilliseconds(it)
            }
        } else {
            null
        }
    }

    fun deleteCacheFile() {
        fileSystem.delete(cachePath)
    }
}