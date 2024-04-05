package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class GetSubscriptionAmountDetails(
        @SerializedName("SubscriptionAmount")
        var subscriptionAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("NoOfDays")
        var days: Int? = 0,
        @SerializedName("StartDate")
        var startDate: String? = null
)