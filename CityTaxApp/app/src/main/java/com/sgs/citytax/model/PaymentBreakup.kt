package com.sgs.citytax.model

import java.io.Serializable
import java.math.BigDecimal

data class PaymentBreakup(
        var paymentMode: String = "",
        var amount: BigDecimal = BigDecimal.ZERO
) : Serializable
