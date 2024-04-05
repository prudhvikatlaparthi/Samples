package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class SaveCustomerProductInterests(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("cusprodinst")
        var customerProductInterests: CustomerProductInterests? = null
)