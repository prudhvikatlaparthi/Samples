package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.PropertyVerificationRequestData

data class ApproveRejectPropertyVerificationReq(
        val context: SecurityContext = SecurityContext(),
        @SerializedName("data")
        var data: PropertyVerificationRequestData? = null
)