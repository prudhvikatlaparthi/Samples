package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.ImpoundmentReceiptTable

data class ImpoundmentReceiptResponse(
        @SerializedName("Table")
        var receiptTable: ArrayList<ImpoundmentReceiptTable> = arrayListOf(),
        @SerializedName("OrgData")
        val orgData: List<OrgData>? = null
)