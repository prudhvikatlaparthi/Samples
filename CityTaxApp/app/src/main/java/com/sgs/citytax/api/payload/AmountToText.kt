package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class AmountToText(
        val context: SecurityContext = SecurityContext(),
        @SerializedName("amount")
        var amount: Double? = 0.0
)