package com.pru.shopping.shared.commonModels

import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val created_at: String? = null,
    val email: String,
    val gender: String,
    val id: Int,
    val name: String,
    val status: String,
    val updated_at: String? = null
)