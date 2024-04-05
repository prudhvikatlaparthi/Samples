package com.pru.hiltarchi.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VUUMXUserOrganization(
    @SerialName("AWSPath")
    val aWSPath: String? = null,
    @SerialName("caption")
    val caption: String?= null,
    @SerialName("crncycode")
    val crncycode: String?= null,
    @SerialName("CurrencySymbol")
    val currencySymbol: String?= null,
    @SerialName("email")
    val email: String?= null,
    @SerialName("logo")
    val logo: Int?= null,
    @SerialName("OrgStateID")
    val orgStateID: Int?= null,
    @SerialName("orgz")
    val orgz: String?= null,
    @SerialName("ph")
    val ph: String?= null,
    @SerialName("site")
    val site: String?= null,
    @SerialName("symbatryt")
    val symbatryt: String?= null
)