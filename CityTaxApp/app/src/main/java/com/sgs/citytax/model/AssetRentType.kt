package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class AssetRentType(
        @SerializedName("AssetRentType")
        var assetRentType: String? = "",
        @SerializedName("AssetRentTypeID")
        var assetRentTypeID: Int? = 0
) {
    override fun toString(): String {
        return if (assetRentType == null) "" else "$assetRentType"
    }
}