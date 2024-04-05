package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.ServiceTaxNoticeReceiptDetails

data class ServiceTaxNoticeResponse(
        @SerializedName("Table")
        var receiptDetails:ArrayList<ServiceTaxNoticeReceiptDetails> = arrayListOf(),
        @SerializedName("OrgData")
        val orgData: List<OrgData>? = null
)