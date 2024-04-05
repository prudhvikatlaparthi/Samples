package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetIndividualTaxCount4Business(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("acctid")
        var primaryKeyValue: Int? = 0
)