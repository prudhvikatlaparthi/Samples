package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetPaymentPeriodForParking(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("ratecycleid")
        var rateCycleID: Int? = 0,
        @SerializedName("stdt")
        var startDate: String? = "",
        @SerializedName("ruleid")
        var ruleID: Int? = 0,
        @SerializedName("prkplcid")
        var parkingPlaceID: Int? = 0,
        @SerializedName("prktypeid")
        var parkingTypeID: Int? = 0
)
