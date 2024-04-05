package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetBusinessOwners(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("Advsrchfilter")
        var ownerSearchFilter: OwnerSearchFilter? = null
)