package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.base.MyApplication

data class GetGroupingSalesReportPayload(
    var context: SecurityContext = SecurityContext(),
    @SerializedName("4mdt")
    var fromDate: String? = "",
    @SerializedName("2dt")
    var toDate: String? = "",
    @SerializedName("securitysal")
    var securitysal: String = "N",
    @SerializedName("payLoadString")
    var payLoadString: String? = null,
    @SerializedName("loginaccid")
    var loginaccid: Int = MyApplication.getPrefHelper().accountId,
)