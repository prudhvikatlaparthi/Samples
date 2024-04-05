package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class AssetSpecsValueSets(
        @SerializedName("Value")
        var value: String? = "",
        @SerializedName("Active")
        var active: String? = "",
        @SerializedName("Default")
        var default: String? = "",
        @SerializedName("SpecificationValueID")
        var specificationValueID: Int? = 0
) {
    override fun toString(): String {
        return value.toString()
    }
}