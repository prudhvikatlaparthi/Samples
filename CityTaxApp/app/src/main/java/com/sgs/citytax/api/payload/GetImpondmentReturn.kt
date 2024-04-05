package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetImpondmentReturn(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("colname")
        var columnName: String? = null,
        @SerializedName("colval")
        var columnValue: String? = null,
        @SerializedName("pgsize")
        var pageSize: Int? = null,
        @SerializedName("pgindex")
        var pageIndex: Int? = null
)