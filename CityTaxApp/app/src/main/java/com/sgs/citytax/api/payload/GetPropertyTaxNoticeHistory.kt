package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetPropertyTaxNoticeHistory(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("taxrulebookcode")
        var taxRuleBookCode: String? = null,
        @SerializedName("vchrno")
        var voucherNo: Int? = 0,
        @SerializedName("PageIndex")
        var pageIndex: Int? = 1,
        @SerializedName("PageSize")
        var pageSize: Int? = 20
)