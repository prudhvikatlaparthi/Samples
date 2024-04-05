package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class CustomerProduct(
        @SerializedName("custid")
        var customerID: Int? = null,
        @SerializedName("prodcode")
        var productCode: String? = null,
        @SerializedName("prod")
        var product: String? = null,
        @SerializedName("MultiInvoice")
        var multiInvoice: String? = null,
        @SerializedName("TaxRuleBookID")
        var taxRuleBookID: Int? = null,
        @SerializedName("TaxRuleBookCode")
        var taxRuleBookCode: String? = null
) {
    override fun toString(): String {
        return "$product"
    }
}