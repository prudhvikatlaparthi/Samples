package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class PaymentByCheque4TicketPayment(
        @SerializedName("data")
        var data: TicketPaymentData? = null,
        var context: SecurityContext = SecurityContext(),
        @SerializedName("chqdetails")
        var chequeDetails: ChequeDetails ?= null,
)
