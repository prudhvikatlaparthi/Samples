package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.ShowTaxNoticeReceiptDetails

data class ShowTaxNoticeResponse(
        @SerializedName("Table")
        var receiptDetails: ArrayList<ShowTaxNoticeReceiptDetails> = arrayListOf(),
        @SerializedName("OrgData")
        val orgData: List<OrgData>? = null
)