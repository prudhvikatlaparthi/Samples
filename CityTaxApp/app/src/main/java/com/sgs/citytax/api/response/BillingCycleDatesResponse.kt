package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class BillingCycleDatesResponse(
        @SerializedName("StartDate")
        var startDate: String? = "",
        @SerializedName("EndDate")
        var endDate: String? = ""
)