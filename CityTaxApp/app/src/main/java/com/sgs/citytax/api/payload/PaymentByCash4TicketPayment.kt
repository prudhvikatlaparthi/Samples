package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import java.math.BigDecimal

data class PaymentByCash4TicketPayment(
        @SerializedName("data")
        var data: TicketPaymentData? = null,
        var context: SecurityContext = SecurityContext()


)
