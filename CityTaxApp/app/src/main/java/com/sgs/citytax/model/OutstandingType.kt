package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class OutstandingType(
        @SerializedName("OutstandingType")
        var outstandingType: String? = "",
        @SerializedName("OutstandingTypeCode")
        var outstandingTypeCode: String? = "",
        @SerializedName("desc")
        var description: String? = ""
) {
    override fun toString(): String {
        return "$outstandingType"
    }
}