package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class DeleteDocument(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("docrefid")
        var primaryKeyValue: Int? = 0
)