package com.pru.judostoreapp.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("address")
    val address: String? = null,
    @SerialName("firstName")
    val firstName: String? = null,
    @SerialName("lastName")
    val lastName: String? = null,
    @SerialName("mobileNumber")
    val mobileNumber: Int? = null,
    @SerialName("panCardNo")
    val panCardNo: Int? = null,
    @SerialName("password")
    val password: String? = null,
    @SerialName("role")
    val role: String? = null,
    @SerialName("userId")
    val userId: Int? = null,
    @SerialName("userName")
    val userName: String? = null
)