package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetPendingAssetBookingRequest(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("pgsize")
        var pageSize:Int?=0,
        @SerializedName("pgindex")
        var pageIndex:Int?=0
)