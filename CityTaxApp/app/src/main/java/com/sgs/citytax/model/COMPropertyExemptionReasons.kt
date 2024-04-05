package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class COMPropertyExemptionReasons(
        @SerializedName("PropertyExemptionReason")
        var propertyExemptionReason: String? = "",
        @SerializedName("PropertyExemptionReasonID")
        var propertyExemptionReasonID: Int? = 0
) {
    override fun toString(): String {
        return propertyExemptionReason.toString()
    }
}