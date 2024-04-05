package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetBusinessTaxDueYearSummary(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("acctid")
        var acountID: Int? = 0
)