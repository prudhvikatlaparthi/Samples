package com.pru.ricknmortykmm.models.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ProfileDto(
    @SerialName("created")
    val created: String? = null,
    @SerialName("episode")
    val episode: List<String>? = null,
    @SerialName("gender")
    val gender: String? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("image")
    val image: String? = null,
    @SerialName("location")
    val locationDto: LocationDto? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("origin")
    val originDto: OriginDto? = null,
    @SerialName("species")
    val species: String? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("type")
    val type: String? = null,
    @SerialName("url")
    val url: String? = null,
)