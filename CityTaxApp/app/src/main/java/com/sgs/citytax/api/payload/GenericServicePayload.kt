package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GenericServicePayload(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("Advsrchfilter")
        var searchFilter: AdvanceSearchFilter? = null
)