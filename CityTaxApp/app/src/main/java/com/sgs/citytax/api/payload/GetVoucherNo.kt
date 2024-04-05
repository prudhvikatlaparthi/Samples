package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetVoucherNo(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("acctid")
        var accountID: Int? = null,
        @SerializedName("TaxRuleBookCode")
        var taxRuleBookCode: String? = null
)