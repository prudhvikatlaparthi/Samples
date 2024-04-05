package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class CRMComplaintSubtype(
        @SerializedName("ComplaintSubtype")
        var complaintSubtype: String? = null,
        @SerializedName("ComplaintID")
        var complaintID: Int? = 0,
        @SerializedName("ComplaintSubtypeID")
        var complaintSubtypeID: Int? = 0,
        @SerializedName("act")
        var act: String? = null,
        @SerializedName("code")
        var code: String? = null
) {
    override fun toString(): String {
        return complaintSubtype.toString()
    }
}