package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.PenaltyWaiveOffDetailsTable
import com.sgs.citytax.model.PenaltyWaiveOffReceiptChangeDetails

data class PenaltyWaiveOffReceiptResponse(
        @SerializedName("Table")
        var penaltyWaiveReceiptChangeDetails: ArrayList<PenaltyWaiveOffReceiptChangeDetails> = arrayListOf(),
        @SerializedName("Table1")
        var penaltyWaiveOffDetailsTable: ArrayList<PenaltyWaiveOffDetailsTable> = arrayListOf(),
        @SerializedName("OrgData")
        val orgData: List<OrgData>? = null
)