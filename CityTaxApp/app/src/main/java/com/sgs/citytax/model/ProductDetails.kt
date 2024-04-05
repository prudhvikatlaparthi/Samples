package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class ProductDetails(
        @SerializedName("act")
        var active: String? = null,
        @SerializedName("cat")
        var category: String? = null,
        @SerializedName("desc")
        var description: String? = null,
        @SerializedName("PricingRule")
        var pricingRule: String? = null,
        @SerializedName("Productcode")
        var productcode: String? = null,
        @SerializedName("Productname")
        var productname: String? = null,
        @SerializedName("DefaultImage")
        var defaultImage: String? = null
) {
    override fun toString(): String {
        return "$productname $productcode"
    }
}
