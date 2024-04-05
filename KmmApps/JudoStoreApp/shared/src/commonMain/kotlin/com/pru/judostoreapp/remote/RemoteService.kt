package com.pru.judostoreapp.remote

import com.pru.judostoreapp.models.User
import com.pru.judostoreapp.utils.Constants.kBaseUrl
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.URLBuilder
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject

class RemoteService(private val enableLog: Boolean) {

    private var httpClient = httpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }

        if (enableLog) {
            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.ALL
            }
        }
    }
        private set

    suspend fun makePostCall(
        endPoint: String, rawBody: User
    ): Result<User> {
        return try {
            val response = httpClient.post {
                url(url = URLBuilder(kBaseUrl + endPoint).build())
                contentType(ContentType.Application.Json)
                setBody(rawBody)
            }
            Result.success(response.body())
        } catch (e: Exception) {
            println("Exception $e")
            Result.failure(e)
        }
    }

}

