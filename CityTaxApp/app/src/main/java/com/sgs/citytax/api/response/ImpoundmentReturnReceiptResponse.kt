package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.ImpoundmentLineSummary
import com.sgs.citytax.model.ImpoundmentReturnReceiptTable

data class ImpoundmentReturnReceiptResponse(
        @SerializedName("ImpoundmentSummary")
        var receiptTable: ArrayList<ImpoundmentReturnReceiptTable> = arrayListOf(),
        @SerializedName("ImpoundmentLineSummary")
        var impoundmentLineSummary: ArrayList<ImpoundmentLineSummary> = arrayListOf(),
        @SerializedName("OrgData")
        val orgData: List<OrgData>? = null
)