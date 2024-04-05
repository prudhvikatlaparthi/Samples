package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetCurrentStock(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("accID")
        var accountID: Int? = 0
)