package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class CRMCartType(
        @SerializedName("CartType")
        var cartType: String? = "",
        @SerializedName("CartTypeCode")
        var cartTypeCode: String? = "",
        @SerializedName("prodcode")
        var productCode: String? = "",
        @SerializedName("CartTypeID")
        var cartTypeID: Int? = 0,
        @SerializedName("act")
        var isActive: String? = ""
) {
    override fun toString(): String {
        return cartType.toString()
    }
}