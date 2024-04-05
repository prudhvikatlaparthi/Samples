package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetTaxInvoicesDetailsByNoticeReferenceNo(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("noticeRefno")
        var noticeReferenceNo: String? = ""
)