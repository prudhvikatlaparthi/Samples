package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.LicenseRenewalNoticeReceiptDetails

data class LicenseRenewakNoticeResponse(
        @SerializedName("Table")
        var receiptDetails: ArrayList<LicenseRenewalNoticeReceiptDetails> = arrayListOf(),
        @SerializedName("OrgData")
        val orgData: List<OrgData>? = null
)