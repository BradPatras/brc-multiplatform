package io.github.bradpatras.basicremoteconfigs.cache

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

internal class DefaultCacheHelper(
    private val cachePath: Path,
    private val fileSystem: FileSystem
) : CacheHelper {
    override suspend fun getCacheConfigs(): JsonObject? = withContext(Dispatchers.IO) {
        // return early if cache file doesn't exist
        try {
            if (!fileSystem.exists(cachePath)) {
                return@withContext null
            }
        } catch (exception: Throwable) {
            return@withContext null
        }

        fileSystem.source(cachePath).use { fileSource ->
            fileSource.buffer().use { bufferedFileSource ->
                val element = Json.Default.parseToJsonElement(bufferedFileSource.readUtf8())
                return@withContext element.jsonObject
            }
        }
    }

    override suspend fun setCacheConfigs(configs: JsonObject): Unit = withContext(Dispatchers.IO) {
        val parent = cachePath.parent ?: Path.DIRECTORY_SEPARATOR.toPath()
        try {
            if (!fileSystem.exists(parent)) {
                fileSystem.createDirectories(parent)
            }
        } catch (exception: Throwable) {
            throw exception
        }

        fileSystem.sink(cachePath).use { fileSink ->
            fileSink.buffer().use { bufferedSink ->
                bufferedSink.writeUtf8(Json.encodeToString(JsonObject.serializer(), configs))
            }
        }
    }

    override fun getLastModified(): Instant? {
        return if (fileSystem.exists(cachePath)) {
            fileSystem.metadata(cachePath).lastModifiedAtMillis?.let {
                Instant.fromEpochMilliseconds(it)
            }
        } else {
            null
        }
    }

    override fun deleteCacheFile() {
        fileSystem.delete(cachePath)
    }
}