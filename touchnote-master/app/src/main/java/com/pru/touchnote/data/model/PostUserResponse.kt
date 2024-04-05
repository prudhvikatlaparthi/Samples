package com.pru.touchnote.data.model

data class PostUserResponse(
    val code: Int,
    val `data`: Data,
    val meta: Meta? = null
)