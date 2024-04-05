package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.CartTaxNoticeDetails
import com.sgs.citytax.model.ROPTaxNoticeDetails

data class CartTaxNoticeResponse(
        @SerializedName("Table")
        var cartTaxNoticeDetails: ArrayList<CartTaxNoticeDetails> = arrayListOf(),
        @SerializedName("OrgData")
        val orgData: List<OrgData>? = null
)