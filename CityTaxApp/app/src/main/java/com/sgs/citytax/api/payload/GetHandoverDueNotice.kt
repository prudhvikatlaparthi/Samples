package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetHandoverDueNotice(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("filterstring")
        var filterString: String? = "",
        @SerializedName("PageIndex")
        var pageIndex: Int? = 0,
        @SerializedName("PageSize")
        var pageSize: Int? = 0
)