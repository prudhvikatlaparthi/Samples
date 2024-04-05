package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetTrackOnTransactionHistory(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("PageIndex")
        var pageIndex: Int?,
        @SerializedName("PageSize")
        var pageSize: Int = 10,
        @SerializedName("stdt")
        var startDate: String? = "",
        @SerializedName("edt")
        var endDate: String? = ""
)