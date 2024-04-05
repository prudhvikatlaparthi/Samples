package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetPenaltyTransactions(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("fltrtype")
        var filterType: String? = null,
        @SerializedName("fltrstr")
        var filterString: String? = null
)