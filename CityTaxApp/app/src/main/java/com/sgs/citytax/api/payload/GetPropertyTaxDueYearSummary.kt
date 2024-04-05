package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetPropertyTaxDueYearSummary(
        val context: SecurityContext = SecurityContext(),
        @SerializedName("taxrulebookcode")
        var taxRuleBookCode: String? = "",
        @SerializedName("vchrno")
        var voucherNo: Int? = 0
)