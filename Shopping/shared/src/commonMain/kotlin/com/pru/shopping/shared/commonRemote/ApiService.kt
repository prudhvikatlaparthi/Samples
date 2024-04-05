package com.pru.shopping.shared.commonRemote

import com.pru.shopping.shared.MyLogger
import com.pru.shopping.shared.commonConstants.Constants.LAUNCHES_ENDPOINT
import com.pru.shopping.shared.commonConstants.Constants.kBASEURL
import com.pru.shopping.shared.commonConstants.Constants.kTODOS
import com.pru.shopping.shared.commonModels.*
import io.ktor.client.*
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json

internal object ApiService {
    private val httpClient = HttpClient {
        install(JsonFeature) {
            val json = Json { ignoreUnknownKeys = true }
            serializer = KotlinxSerializer(json)
        }
        install(Logging) {
            logger = CustomLogger()
            level = LogLevel.BODY
        }
    }

    internal suspend fun getAllLaunches(): List<RocketLaunch> {
        return httpClient.get(LAUNCHES_ENDPOINT)
    }

    internal suspend fun getTodos(): List<TodoItem> {
        return httpClient.get(kBASEURL + kTODOS)
    }

    internal suspend fun getShopByCategories(): List<String> {
     delay(1000)
     return listOf("Bestsellers", "Offers", "Grains", "Pulses", "Oils", "Spices", "Vegetables")
    }

    internal suspend fun postUser(user: Data): UserResponse {
        return httpClient.post {
            url(url = URLBuilder("https://gorest.co.in/public-api/users").build())
            headers {
                append("Authorization","Bearer 7643284989ed91683c608a8da3e5748f186d0d148b459d76554198edd238de9a")
                append("Content-Type","application/json")
            }
            body = user
        }
    }
}

internal class CustomLogger : Logger, MyLogger() {
    override fun log(message: String) {
        debugLogger(logMessage = message)
    }
}

