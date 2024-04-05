package com.pru.touchnote.data.model

data class UserResponse(
    val code: Int,
    val `data`: MutableList<Data>,
    val meta: Meta? = null
)