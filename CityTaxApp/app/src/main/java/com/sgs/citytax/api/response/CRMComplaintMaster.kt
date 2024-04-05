package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class CRMComplaintMaster(
        @SerializedName("Complaint")
        var complaint: String? = null,
        @SerializedName("ComplaintID")
        var complaintID: Int? = 0,
        @SerializedName("code")
        var code: String? = null,
        @SerializedName("act")
        var act: String? = null
) {
    override fun toString(): String {
        return complaint.toString()
    }
}