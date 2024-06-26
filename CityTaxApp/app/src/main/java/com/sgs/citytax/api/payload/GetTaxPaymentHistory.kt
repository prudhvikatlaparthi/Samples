package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetTaxPaymentHistory(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("custid")
        var customerId: Int? = null,
        @SerializedName("prodcode")
        var productCode: String? = null,
        @SerializedName("vchrno")
        var voucherNo: Int? = null,
        @SerializedName("pgindex")
        var pageIndex: Int? = 0,
        @SerializedName("pgsize")
        var pageSize: Int? = 0
)
