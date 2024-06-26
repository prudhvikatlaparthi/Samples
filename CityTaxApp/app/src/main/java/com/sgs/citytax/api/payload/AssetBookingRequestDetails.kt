package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class AssetBookingRequestDetails(
        val context:SecurityContext = SecurityContext(),
        @SerializedName("bookingrequestid")
        var bookingRequestId:Int?=0,
        @SerializedName("recptcode")
        var receiptCode: String? = ""
)