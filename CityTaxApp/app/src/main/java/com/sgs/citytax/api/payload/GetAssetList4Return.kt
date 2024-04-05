package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetAssetList4Return(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("pageindex")
        var pageIndex:Int?=0,
        @SerializedName("pagesize")
        var pageSize:Int?=0
)