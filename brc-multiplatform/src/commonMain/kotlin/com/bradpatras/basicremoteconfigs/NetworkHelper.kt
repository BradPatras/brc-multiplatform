package com.bradpatras.basicremoteconfigs

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

internal class HttpRequestHelper {
    companion object {
        private val client = HttpClient(CIO)

        suspend fun makeGetRequest(
            url: String,
            customHeaders: HashMap<String, String>
        ): JsonObject? = withContext(Dispatchers.IO) {
            try {
                val response: HttpResponse = client.get(url) {
                    headers {
                        customHeaders.forEach { (key, value) ->
                            append(key, value)
                        }
                    }
                }

                if (response.status.isSuccess()) {
                    val responseBody: String = response.bodyAsText()
                    return@withContext Json.decodeFromString<JsonObject>(responseBody)
                } else {
                    return@withContext null
                }
            } catch (exception: Throwable) {
                throw exception
            }
        }
    }
}

