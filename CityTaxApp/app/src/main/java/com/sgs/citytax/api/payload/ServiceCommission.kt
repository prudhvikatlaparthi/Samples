package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class ServiceCommission(
    @SerializedName("commpct")
    var commissionPercentage: BigDecimal? = BigDecimal.ZERO,
    @SerializedName("commamt")
    var commissionAmount: BigDecimal? = BigDecimal.ZERO,
)
