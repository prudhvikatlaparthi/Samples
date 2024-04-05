package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class AssetSpecifications(
        @SerializedName("AssetID")
        var assetID: Int? = 0,
        @SerializedName("specid")
        var specificationID: Int? = 0,
        @SerializedName("val")
        var value: String? = "",
        @SerializedName("SpecificationValueID")
        var specificationValueID: Int? = 0,
        @SerializedName("DateValue")
        var dateValue: String? = null
)