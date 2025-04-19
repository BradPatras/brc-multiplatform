package com.bradpatras.basicremoteconfigs.network

import kotlinx.serialization.json.JsonObject

internal interface NetworkHelper {
    suspend fun requestJson(url: String, customHeaders: HashMap<String, String>): JsonObject?
}

