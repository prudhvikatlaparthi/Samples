package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class ProductTypes(
        @SerializedName("TypeName")
        var typeName: String? = null,
        @SerializedName("prodcode")
        var productCode: String? = null,
        @SerializedName("prod")
        var product: String? = null,
        @SerializedName("TaxRuleBookCode")
        var taxRuleBookCode: String? = null,
        @SerializedName("prodtypcode")
        var prodtypcode: String? = null
) {
    override fun toString(): String {
        return "$product"
    }
}