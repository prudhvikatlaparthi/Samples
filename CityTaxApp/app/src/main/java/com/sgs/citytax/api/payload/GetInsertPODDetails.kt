package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetInsertPODDetails(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("publicdomainoccupydetails")
        var insertPODDetails: InsertPODDetails? = null
)
