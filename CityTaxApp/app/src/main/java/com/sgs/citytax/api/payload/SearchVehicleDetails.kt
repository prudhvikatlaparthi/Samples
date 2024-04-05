package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class SearchVehicleDetails(
        val context: SecurityContext = SecurityContext(),
        @SerializedName("filterstring")
        var filterString: String? = "",
        @SerializedName("pageindex")
        var pageIndex: Int? = 0,
        @SerializedName("pagesize")
        var pageSize: Int? = 0
)