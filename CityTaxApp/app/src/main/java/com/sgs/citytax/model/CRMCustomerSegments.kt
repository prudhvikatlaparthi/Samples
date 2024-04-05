package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class CRMCustomerSegments(
        @SerializedName("seg")
        var segment: String? = "",
        /*@SerializedName("act")
        var active: String? = "",*/
        @SerializedName("segid")
        var segmentId: Int? = 0,
        @SerializedName("defntn")
        var defntn: String? = ""
) {
    override fun toString(): String {
        return segment.toString()
    }
}