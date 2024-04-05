package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class StoreAndPayParkingTicketResponse(
        @SerializedName("TicketID")
        var ticketID: Int? = 0,
        @SerializedName("InvoiceID")
        var invoiceID: Int? = 0,
        @SerializedName("AdvanceReceivedID")
        var advanceReceivedID: Int? = 0
)