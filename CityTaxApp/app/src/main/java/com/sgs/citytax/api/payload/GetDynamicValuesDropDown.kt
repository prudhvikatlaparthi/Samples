package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetDynamicValuesDropDown(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("Advsrchfilter")
        var dropdownSearchFilter: AdvanceSearchFilter? = null
)