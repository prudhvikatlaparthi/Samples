package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class CRMServiceType(
        @SerializedName("ServiceTypeID")
        var serviceTypeID: Int? = 0,
        @SerializedName("ServiceType")
        var serviceType: String? = "",
        @SerializedName("ServiceTypeCode")
        var serviceTypeCode: String? = "",
        @SerializedName("act")
        var isActive: String? = ""
) {
    override fun toString(): String {
        return serviceType.toString()
    }
}