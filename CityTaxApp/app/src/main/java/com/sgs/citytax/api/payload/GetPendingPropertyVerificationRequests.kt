package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.PropertyDetailLocation

data class GetPendingPropertyVerificationRequests(
        val context: SecurityContext = SecurityContext(),
        @SerializedName("pageindex")
        var pageIndex: Int? = 0,
        @SerializedName("pagesize")
        var pageSize: Int? = 0,
        @SerializedName("data")
        var data: PropertyVerificationData? = null
)