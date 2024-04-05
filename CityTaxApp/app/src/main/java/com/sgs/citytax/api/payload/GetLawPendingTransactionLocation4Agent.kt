package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetLawPendingTransactionLocation4Agent(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("AgentAccountID")
        var accountId: Int? = 0,
        @SerializedName("pagesize")
        var pagesize: Int? = 0,
        @SerializedName("pageindex")
        var pageindex: Int? = 0,
        @SerializedName("isFromAndroid")
        var isFromAndroid: String = "Y",
        @SerializedName("fltrdata")
        var fltrdata: ImpondmentMapFilterData?=null
)