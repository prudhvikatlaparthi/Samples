package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetBusinessTransactionHistory(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("srchfilter")
        var searchFilter: SearchFilter? = null
)