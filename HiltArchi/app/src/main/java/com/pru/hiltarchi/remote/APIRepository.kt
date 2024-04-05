package com.pru.hiltarchi.remote

import com.pru.hiltarchi.models.TodoList
import com.pru.hiltarchi.utils.APIEndPoints.kBASEURL
import com.pru.hiltarchi.utils.APIEndPoints.kTODOS
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.*
import javax.inject.Inject

@KtorExperimentalAPI
class APIRepository @Inject constructor(
    private val ktorHttpClient: HttpClient
) {
    suspend fun getTodos(): TodoList {
        val res = ktorHttpClient.request<TodoList>(urlString = kBASEURL + kTODOS) {
            method = HttpMethod.Get
            headers {
                append("My-Custom-Header", "HeaderValue")
            }
        }
        return res

    }
}
