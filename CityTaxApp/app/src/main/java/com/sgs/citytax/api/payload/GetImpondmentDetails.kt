package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetImpondmentDetails(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("id")
        var id: String? = ""
)