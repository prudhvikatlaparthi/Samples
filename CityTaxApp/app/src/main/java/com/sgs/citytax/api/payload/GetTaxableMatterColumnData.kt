package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetTaxableMatterColumnData(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("taskcode")
        var taskCode: String? = ""
)