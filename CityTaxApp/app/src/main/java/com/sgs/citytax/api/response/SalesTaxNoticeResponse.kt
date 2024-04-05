package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.SalesProductDetails
import com.sgs.citytax.model.SalesTaxNoticeDetails

data class SalesTaxNoticeResponse(
        @SerializedName("Table")
        var salesTaxNoticeDetails: ArrayList<SalesTaxNoticeDetails> = arrayListOf(),
        @SerializedName("Table1")
        var productDetails: ArrayList<SalesProductDetails> = arrayListOf(),
        @SerializedName("OrgData")
        val orgData: List<OrgData>? = null
)