package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class InsertRequestCashDeposit(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("acctid")
        var accountID: Int? = 0
)