package com.pru.ktorteams


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    @SerialName("body")
    var body: String,
    @SerialName("email")
    var email: String,
    @SerialName("id")
    var id: Int,
    @SerialName("name")
    var name: String,
    @SerialName("postId")
    var postId: Int
)