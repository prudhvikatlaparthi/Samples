package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class ServiceRequestBookingDetails(
        val context: SecurityContext = SecurityContext(),
        @SerializedName("svcreqno")
        var serviceRequestNo: Int? = 0,
        @SerializedName("recptcode")
        var receiptCode: String? = ""
)