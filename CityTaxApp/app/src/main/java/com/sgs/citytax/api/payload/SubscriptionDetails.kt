package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class SubscriptionDetails(
        var context: SecurityContext? = SecurityContext(),
        @SerializedName("subsmodelid")
        var modelID: Int? = 0,
        @SerializedName("usrid")
        var userID: String? = null
)