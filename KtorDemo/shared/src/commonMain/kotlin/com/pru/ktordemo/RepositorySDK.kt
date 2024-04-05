package com.pru.ktordemo

import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*

object RepositorySDK {
    private const val kBASE_URL = "https://jsonplaceholder.typicode.com/"
    private const val kPOSTS_END_POINT = "posts"
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

    @Throws(Exception::class)
    suspend fun getUsers(): List<Post> {
        val data =
            httpClient.get<List<Post>>(url = URLBuilder(kBASE_URL + kPOSTS_END_POINT).build())
        return data
    }

    @Throws(Exception::class)
    suspend fun getData(endPoint: String) : CheckPart {
        val result = httpClient.get<CheckPart>(url = URLBuilder("https://jsonkeeper.com/b/$endPoint").build())
        return result
    }

}