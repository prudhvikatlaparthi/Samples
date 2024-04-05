package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.PaymentPeriod
import java.math.BigDecimal

data class GetPaymentPeriod(
        @SerializedName("PaymentPeriod")
        var paymentPeriods: PaymentPeriod? = null,
        @SerializedName("amt")
        var amount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("MinPayAmount")
        var minPayAmount: BigDecimal? = BigDecimal.ZERO
)