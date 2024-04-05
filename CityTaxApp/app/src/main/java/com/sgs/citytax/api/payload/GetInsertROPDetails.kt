package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetInsertROPDetails(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("rightofplacedetails")
        var insertROPDetails: InsertROPDetails? = null
)
