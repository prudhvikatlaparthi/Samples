package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class PaymentDate(
        @SerializedName("strtdt")
        var startDate: String? = "",
        @SerializedName("enddt")
        var endDate: String? = ""
)