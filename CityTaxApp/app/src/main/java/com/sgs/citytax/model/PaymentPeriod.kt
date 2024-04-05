package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.PaymentDate
import java.math.BigDecimal

data class PaymentPeriod(
        @SerializedName("Period")
        var period: Int = 0,
        @SerializedName("Dates")
        var paymentDate: List<PaymentDate>? = arrayListOf()
)