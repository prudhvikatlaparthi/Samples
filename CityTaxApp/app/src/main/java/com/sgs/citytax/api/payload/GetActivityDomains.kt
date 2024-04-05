package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetActivityDomains(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("prodcode")
        var productCode: String? = "",
        @SerializedName("acctid")
        var businessAccountID: Int? = null
)