package com.pru.hiltarchi.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Stor(
    @SerialName("context")
    val context: Context?,
    @SerialName("header")
    val header: Header?,
    @SerialName("is_andr")
    val isAndr: Boolean?,
    @SerialName("is_pos")
    val isPos: Boolean?,
    @SerialName("lines")
    val lines: List<Line>?,
    @SerialName("order")
    val order: Order?
)