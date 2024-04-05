package com.pru.hiltarchi.models

import kotlinx.serialization.Serializable

@Serializable
data class TodoList(val data: List<TodoItem>)
