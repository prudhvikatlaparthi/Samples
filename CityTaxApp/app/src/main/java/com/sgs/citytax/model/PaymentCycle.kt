package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class PaymentCycle(
        @SerializedName("PaymentCycle")
        var paymentCycle: String? = "",
        @SerializedName("DurationPrice")
        var durationPrice: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("DistancePrice")
        var distancePrice: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("DurationRate")
        var durationRate: String? = "",
        @SerializedName("DistanceRate")
        var distanceRate: String? = ""
)