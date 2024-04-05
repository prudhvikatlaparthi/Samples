package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class ShowAndHotelBillingAndPricingResponse(
        @SerializedName("BillingCycle")
        var billingCycle: String? = "",
        @SerializedName("StartDate")
        var startDate: String? = "",
        @SerializedName("EndDate")
        var endDate: String? = "",
        @SerializedName("Rate")
        var rate: String? = "",
        @SerializedName("ShowRate")
        var showRate: String? = "",
        @SerializedName("AllowShowCount")
        var allowShowCount: String? = ""
)