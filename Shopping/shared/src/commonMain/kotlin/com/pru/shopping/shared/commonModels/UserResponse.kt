package com.pru.shopping.shared.commonModels

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val code: Int,
    val `data`: Data,
    val meta: Meta? = null
)