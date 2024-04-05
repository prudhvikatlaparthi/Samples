package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class SearchPendingLicenses(
        val context: SecurityContext = SecurityContext(),
        @SerializedName("pageindex")
        var pageindex: Int? = 1,
        @SerializedName("pagesize")
        var pagesize: Int? = 10,
        @SerializedName("fltrstr")
        var filterString: String? = ""
)