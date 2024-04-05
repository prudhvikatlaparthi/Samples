package com.pru.ricknmortykmm.remote

import com.pru.ricknmortykmm.utils.ApiState
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

class ApiService {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }
    }

    @Throws(Exception::class)
    inline fun <reified T> callAPI(url: String) : Flow<ApiState<T>> = flow {
        emit(ApiState.Loading())
        kotlin.runCatching {
            val response: T = httpClient.get(url).body()
            response
        }.onSuccess {
            emit(ApiState.Success(it))
        }.onFailure {
            emit(ApiState.Failure(it.message))
        }
    }
}