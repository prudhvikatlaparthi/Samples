package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext


data class GetDropdownFiltersForBusinessSearch(
        @SerializedName("context")
        var context: SecurityContext = SecurityContext(),
        @SerializedName("tablname")
        var tablname: String? = null
)


