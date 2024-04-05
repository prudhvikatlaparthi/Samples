package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class PropertyTreePayload(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("proprtyid")
        var proprtyid: Int? = null
)