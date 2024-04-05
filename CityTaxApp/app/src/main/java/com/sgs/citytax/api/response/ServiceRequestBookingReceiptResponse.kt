package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.ServiceBookingAdvanceReceiptTable

data class ServiceRequestBookingReceiptResponse(
        @SerializedName("ServiceRequestBookingDetails")
        var bookingAdvanceReceiptTable:ArrayList<ServiceBookingAdvanceReceiptTable> = arrayListOf(),
        @SerializedName("OrgData")
        val orgData: List<OrgData>? = null
)