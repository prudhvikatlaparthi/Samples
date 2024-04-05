package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetNotesDetails(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("tablname")
        var tableName: String = "",
        @SerializedName("prykeyval")
        var primaryKeyValue: String = ""
)