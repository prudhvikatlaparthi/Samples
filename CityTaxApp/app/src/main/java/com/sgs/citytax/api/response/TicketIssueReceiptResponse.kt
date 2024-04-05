package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.TicketIssueReceiptTable

data class TicketIssueReceiptResponse(
        @SerializedName("Table")
        var receiptDetails:ArrayList<TicketIssueReceiptTable> = arrayListOf(),
        @SerializedName("OrgData")
        val orgData: List<OrgData>? = null
)