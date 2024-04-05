package com.pru.shopping.shared.commonModels

import kotlinx.serialization.Serializable

@Serializable
data class TodoItem(
    val completed: Boolean? = false,
    val id: Int? = null,
    val title: String? = null,
    val userId: Int? = null
)