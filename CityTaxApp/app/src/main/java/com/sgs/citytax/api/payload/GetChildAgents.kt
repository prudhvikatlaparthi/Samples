package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetChildAgents(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("query")
        var query: String? = null
)