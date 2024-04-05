package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetComplaintLocations(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("pagesize")
        var pagesize: Int? = 0,
        @SerializedName("pageindex")
        var pageindex: Int? = 0,
        @SerializedName("isFromAndroid")
        var isFromAndroid: String = "Y"
)