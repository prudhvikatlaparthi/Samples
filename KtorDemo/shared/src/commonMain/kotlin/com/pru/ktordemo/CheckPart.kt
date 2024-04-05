package com.pru.ktordemo


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CheckPart(
    @SerialName("bigDecimal")
    val bigDecimal: Double? = null,
    @SerialName("bigInt")
    val bigInt: Long? = null
)