package com.pru.ktordemo


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Post(
    @SerialName("body")
    var body: String,
    @SerialName("id")
    var id: Int,
    @SerialName("title")
    var title: String,
    @SerialName("userId")
    var userId: Int
)