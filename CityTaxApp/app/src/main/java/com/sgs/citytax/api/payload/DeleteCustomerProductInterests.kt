package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class DeleteCustomerProductInterests(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("custid")
        var customerId: Int = 0,
        @SerializedName("prodcode")
        var productCode: String? = null
)