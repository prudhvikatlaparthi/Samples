package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.base.MyApplication

data class AdjustmentListDetails (
    @SerializedName("acctid")
    var accountId: Int? = MyApplication.getPrefHelper().accountId,
    var context: SecurityContext = SecurityContext(),
    @SerializedName("pageindex")
    var pageIndex: Int? = 0,
    @SerializedName("pagesize")
    var pageSize: Int? = 0,
    @SerializedName("4mdt")
    var fromDate: String? = "",
    @SerializedName("2dt")
    var toDate: String? =  ""

)
