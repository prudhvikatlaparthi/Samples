package com.pru.hiltarchi.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchProductItem(
    @SerialName("catid")
    val catid: String?,
    @SerialName("PopularityCount")
    val popularityCount: Int?,
    @SerialName("prod")
    val prod: String?,
    @SerialName("prodcode")
    val prodcode: String?
)