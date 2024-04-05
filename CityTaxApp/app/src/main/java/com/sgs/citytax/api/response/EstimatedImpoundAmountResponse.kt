package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class EstimatedImpoundAmountResponse(
        @SerializedName("ImpoundmentCharge")
        var impoundmentCharge: BigDecimal = BigDecimal.ZERO,
        @SerializedName("ViolationCharge")
        var violationCharge: BigDecimal = BigDecimal.ZERO,
        @SerializedName("MinPayAmount")
        var minPayAmount: BigDecimal = BigDecimal.ZERO,
        @SerializedName("PaymentAmount")
        var paymentAmount: BigDecimal = BigDecimal.ZERO
)
