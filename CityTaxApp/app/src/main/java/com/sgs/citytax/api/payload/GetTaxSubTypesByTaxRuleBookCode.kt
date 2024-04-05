package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetTaxSubTypesByTaxRuleBookCode(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("taxrulebookcode")
        var taxRuleBookCode: String? = null
)