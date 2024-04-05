package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.ParkingTicketDetails

data class ParkingTicketReceiptResponse(
        @SerializedName("Table")
        var receiptDetails: ArrayList<ParkingTicketDetails> = arrayListOf(),
        @SerializedName("OrgData")
        val orgData: List<OrgData>? = null
)