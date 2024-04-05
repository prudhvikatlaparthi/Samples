package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.base.MyApplication

data class GetAgentStatement(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("agtID")
        var agentID: Int = MyApplication.getPrefHelper().accountId,
        @SerializedName("4rmdt")
        var fromDate: String? = null,
        @SerializedName("2dt")
        var toDate: String? = null
)
