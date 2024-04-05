package com.pru.misc.utils

import com.pru.misc.model.PostsResponse
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json

object AppUtils {
    private val jsonBuilder = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun PostsResponse.mapToString(): String {
        return jsonBuilder.encodeToString(PostsResponse.serializer(), this)
    }

    fun <T> DeserializationStrategy<T>.mapToObject(value: String): T {
        return jsonBuilder.decodeFromString(this, value)
    }
}