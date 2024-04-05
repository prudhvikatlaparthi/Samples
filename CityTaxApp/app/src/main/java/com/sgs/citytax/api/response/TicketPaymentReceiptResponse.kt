package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.TicketImpoundDetails
import com.sgs.citytax.model.TicketPenaltyDetails
import com.sgs.citytax.model.TicketReceiptDetails
import com.sgs.citytax.model.TicketViolationDetails

data class TicketPaymentReceiptResponse(
        @SerializedName("Table")
        var receiptDetails: ArrayList<TicketReceiptDetails> = arrayListOf(),
        @SerializedName("Table1")
        var violationDetails: ArrayList<TicketViolationDetails> = arrayListOf(),
        @SerializedName("Table2")
        var impoundDetails: ArrayList<TicketImpoundDetails> = arrayListOf(),
        @SerializedName("Table3")
        var penaltyDetails: ArrayList<TicketPenaltyDetails> = arrayListOf(),
        @SerializedName("OrgData")
        val orgData: List<OrgData>? = null
)