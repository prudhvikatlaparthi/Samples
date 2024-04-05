package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.UMXUserPolicyDetails

data class InsertUserPolicyDetails(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("data")
        var userPolicyDetails: UMXUserPolicyDetails? = null
)