package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class DeleteNote(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("noteid")
        var primaryKeyValue: Int? = 0
)