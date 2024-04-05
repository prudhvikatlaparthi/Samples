package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.LicenseRenewalNoticeReceiptDetails
import com.sgs.citytax.model.TaxReceiptsDetails

data class TaxReceiptsResponse(
        @SerializedName("Table")
        var taxReceiptsDetails: ArrayList<TaxReceiptsDetails> = arrayListOf(),
        var taxRuleBookCode: String? = "",
        @SerializedName("Table1")
        var receiptDetails: ArrayList<LicenseRenewalNoticeReceiptDetails> = arrayListOf(),
        @SerializedName("OrgData")
        val orgData: List<OrgData>? = null
)