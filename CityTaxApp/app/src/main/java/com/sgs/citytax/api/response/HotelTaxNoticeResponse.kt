package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.HotelTaxNoticeReceiptDetails

data class HotelTaxNoticeResponse(
        @SerializedName("Table")
        var receiptDetails:ArrayList<HotelTaxNoticeReceiptDetails> = arrayListOf(),
        @SerializedName("OrgData")
        val orgData: List<OrgData>? = null
)