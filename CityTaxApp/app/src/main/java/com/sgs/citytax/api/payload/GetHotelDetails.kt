package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetHotelDetails(
        val context: SecurityContext = SecurityContext(),
        @SerializedName("orgzid")
        var organisationID: Int? = 0,
        @SerializedName("pageindex")
        var pageIndex: Int? = 0,
        @SerializedName("pagesize")
        var pageSize: Int? = 0
)