package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetSubscriptionDetails(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("acctid")
        var acctid: String? = "",
        @SerializedName("usrid")
        var usrid: String? = ""
)