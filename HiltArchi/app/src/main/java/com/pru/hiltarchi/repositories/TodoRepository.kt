package com.pru.hiltarchi.repositories

import androidx.lifecycle.LiveData
import com.pru.hiltarchi.models.TodoItem
import com.pru.hiltarchi.models.TodoList
import com.pru.hiltarchi.remote.ApiService
import javax.inject.Inject

class TodoRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getTodos(): List<TodoItem> = apiService.getTodos()

    suspend fun getTodoDetails(id : Int): TodoItem = apiService.getTodoDetails(id)
}