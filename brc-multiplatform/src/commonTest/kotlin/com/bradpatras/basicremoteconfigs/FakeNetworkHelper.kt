package com.bradpatras.basicremoteconfigs

import com.bradpatras.basicremoteconfigs.network.NetworkHelper
import kotlinx.serialization.json.JsonObject

class FakeNetworkHelper(
    private val configs: JsonObject? = null
): NetworkHelper {
    override suspend fun requestJson(
        url: String,
        customHeaders: HashMap<String, String>
    ): JsonObject? = configs
}

class FakeThrowingNetworkHelper(): NetworkHelper {
    override suspend fun requestJson(
        url: String,
        customHeaders: HashMap<String, String>
    ): JsonObject? {
        throw Exception()
    }

}