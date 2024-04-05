package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetPendingLicenses4Agent(
        val context: SecurityContext = SecurityContext(),
        @SerializedName("agentaccountid")
        var agentAccountId: Int? = 0,
        @SerializedName("PageIndex")
        var pageIndex: Int?,
        @SerializedName("PageSize")
        var pageSize: Int = 10
)