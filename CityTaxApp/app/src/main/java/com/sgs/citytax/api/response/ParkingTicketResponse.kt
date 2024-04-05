package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class ParkingTicketResponse(
        @SerializedName("TicketID")
        var ticketId: Int? = 0,
        @SerializedName("InvoiceID")
        var invoiceId: Int? = 0
)