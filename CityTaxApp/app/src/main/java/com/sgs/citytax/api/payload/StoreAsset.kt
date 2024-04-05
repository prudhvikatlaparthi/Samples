package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.AssetSpecifications
import com.sgs.citytax.model.GeoAddress

data class StoreAsset(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("asset", alternate = ["Assets"])
        var asset: Asset? = null,
        @SerializedName("geoaddress")
        var geoAddress: GeoAddress? = null,
        @SerializedName("assetspecs", alternate = ["Specifications"])
        var assetSpecs: List<AssetSpecifications> = arrayListOf()
)