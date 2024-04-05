package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.CartTaxReceiptDetails
import com.sgs.citytax.model.TaxTypesDetails


data class CartTaxReceiptResponse(
        @SerializedName("Table")
        var taxReceiptsDetails: ArrayList<CartTaxReceiptDetails> = arrayListOf(),
        @SerializedName("Table1")
        var taxTypes: ArrayList<TaxTypesDetails> = arrayListOf(),
        var taxRuleBookCode: String? = "",
        @SerializedName("OrgData")
        val orgData: List<OrgData>? = null
)