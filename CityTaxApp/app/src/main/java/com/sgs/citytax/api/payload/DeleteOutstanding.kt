package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class DeleteOutstanding(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("initialoutstandingid")
        var initialOutstandingID: Int? = null
)