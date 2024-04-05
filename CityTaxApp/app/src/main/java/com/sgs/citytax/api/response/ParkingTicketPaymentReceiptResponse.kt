package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.*

data class ParkingTicketPaymentReceiptResponse(
        @SerializedName("Table")
        var receiptDetails: ArrayList<TicketReceiptDetails> = arrayListOf(),
        @SerializedName("Table1")
        var parkingDetails: ArrayList<ParkingDetails> = arrayListOf(),
        @SerializedName("Table2")
        var penaltyDetails: ArrayList<TicketPenaltyDetails> = arrayListOf(),
        @SerializedName("OrgData")
        val orgData: List<OrgData>? = null
)