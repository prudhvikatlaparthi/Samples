package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class ListDueNotice(
    var context: SecurityContext? = SecurityContext(),
    @SerializedName("acctid")
    var accountId: Int? = 0,
    @SerializedName("pagesize")
    var pageSize: Int? = 0,
    @SerializedName("pageindex")
    var pageIndex: Int? = 0
)