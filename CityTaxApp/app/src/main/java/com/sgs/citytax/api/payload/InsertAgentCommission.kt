package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.CommissionDetails

data class InsertAgentCommission(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("commission")
        var commissionDetails: CommissionDetails? = null
)