package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class LastBillingCycleActualAmount(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("acctid")
        var taxPayerAccountID: Int = 0,
        @SerializedName("orgzid")
        var taxPayerOrganizationID: Int = 0,
        @SerializedName("strtdt")
        var strtdt: String = ""
)