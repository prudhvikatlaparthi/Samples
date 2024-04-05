package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class NewTicketCreationResponse(
        @SerializedName("TicketID")
        var ticketID: Int = 0,
        @SerializedName("InvoiceID")
        var invoiceID: Int = 0
)