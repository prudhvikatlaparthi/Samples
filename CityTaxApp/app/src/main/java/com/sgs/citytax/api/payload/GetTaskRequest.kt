package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetTaskRequest(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("Advsrchfilter")
        var advanceSearchFilter: AdvanceSearchFilterTask? = null,
        @SerializedName("TableDetails")
        var tableDetails: TableDetails? = null
)
