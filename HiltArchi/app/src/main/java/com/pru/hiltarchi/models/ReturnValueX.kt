package com.pru.hiltarchi.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReturnValueX(
    @SerialName("VU_UMX_UserOrganizations")
    val vUUMXUserOrganizations: List<VUUMXUserOrganization>?
)