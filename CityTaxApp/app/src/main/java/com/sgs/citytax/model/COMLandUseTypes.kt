package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class COMLandUseTypes(
        @SerializedName("LandUseType")
        var LandUseType: String? = "",
        @SerializedName("LandUseTypeCode")
        var LandUseTypeCode: String? = "",
        @SerializedName("act")
        var act: String? = "",
        @SerializedName("LandUseTypeID")
        var LandUseTypeID: Int? = null
) {
    override fun toString(): String {
        return LandUseType.toString()
    }
}