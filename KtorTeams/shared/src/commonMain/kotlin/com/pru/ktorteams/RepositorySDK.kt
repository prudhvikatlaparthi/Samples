package com.pru.ktorteams

import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*

object RepositorySDK {
    private const val kBASE_URL = "https://jsonplaceholder.typicode.com/"
    private const val kENDPOINT_POSTS = "posts"
    private val httpClient = HttpClient {
        install(JsonFeature) {
            val json = kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
                isLenient = true
            }
            serializer = KotlinxSerializer(json)
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
    }

    suspend fun getPosts(): List<Post> {
        val response =
            httpClient.get<List<Post>>(url = URLBuilder(kBASE_URL + kENDPOINT_POSTS).build())
        return response
    }
}