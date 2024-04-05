package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetTaxPayerDetails(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("filterstring")
        var filterString: String? = "",
        @SerializedName("pageindex")
        var pageIndex: Int? = 0,
        @SerializedName("pagesize")
        var pageSize: Int? = 0,
        @SerializedName("inactive")
        var inactive: String? = null,
        @SerializedName("mobverifd")
        var mobverifd: String? = null,
        @SerializedName("emailverifd")
        var emailverifd: String? = null
)
