package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.OutstandingWaiveOffReceiptDetails

data class InitialOutstandingWaiveOffReceiptResponse(
        @SerializedName("Table")
        var receiptDetails:ArrayList<OutstandingWaiveOffReceiptDetails> = arrayListOf(),
        @SerializedName("OrgData")
        val orgData: List<OrgData>? = null
)