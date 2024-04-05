package com.pru.hiltarchi.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Context(
    @SerialName("acctid")
    val acctid: Int?,
    @SerialName("crncycode")
    val crncycode: String?,
    @SerialName("domain")
    val domain: String?,
    @SerialName("langcode")
    val langcode: String?,
    @SerialName("lat")
    val lat: String?,
    @SerialName("loggeduserid")
    val loggeduserid: String?,
    @SerialName("long")
    val long: String?,
    @SerialName("rlcode")
    val rlcode: String?,
    @SerialName("usrorgbrid")
    val usrorgbrid: Int?,
    @SerialName("usrorgid")
    val usrorgid: Int?
)