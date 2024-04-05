package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class GetGamingMachineTypes(
        @SerializedName("GamingMachineType")
        var gamingMachineType: String? = null,
        @SerializedName("GamingMachineTypeCode")
        var gamingMachineTypeCode: String? = null,
        @SerializedName("prodcode")
        var productCode: String? = null,
        @SerializedName("act")
        var active: String? = null,
        @SerializedName("GamingMachineTypeID")
        var gamingMachineTypeID: Int? = 0
) {
    override fun toString(): String {
        return gamingMachineType.toString()
    }
}