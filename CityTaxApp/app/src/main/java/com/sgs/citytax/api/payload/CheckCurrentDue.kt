package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class CheckCurrentDue(
        val context: SecurityContext = SecurityContext(),
        @SerializedName("acctid")
        var accountId: Int? = null,
        @SerializedName("taxrlebokcde")
        var taxRuleBookCode: String? = null,
        @SerializedName("vchrno")
        var vchrno: Int? = null
)