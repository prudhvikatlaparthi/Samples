package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.AssetRentAndReturnReceiptDetails

data class AssetRentAndReturnReceiptResponse(
        @SerializedName("Table")
        var receiptDetails: ArrayList<AssetRentAndReturnReceiptDetails> = arrayListOf(),
        var taxRuleBookCode:String?="",
        @SerializedName("OrgData")
        val orgData: List<OrgData>? = null
)