package com.pru.hiltarchi.remote

import com.pru.hiltarchi.models.TodoItem
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    companion object {
        const val BASE_URL = "https://jsonplaceholder.typicode.com/"
    }

    @GET("todos")
    suspend fun getTodos(): List<TodoItem>

    @GET("todos/{id}")
    suspend fun getTodoDetails(@Path("id") id: Int): TodoItem
}