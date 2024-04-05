package com.example.demoktorapi.shared.userProfile

import kotlinx.serialization.Serializable

@Serializable
data class UserDtoRequest<T>(
    var data: T,
    var profiles_url: String,
    var responseMessage: String,
    var responseCode: String
)