package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.payload.AssetDetails


data class AssetDetailsBySycotax(
        @SerializedName("AssetDetails")
        var assetDetails: AssetDetails?= null,
        @SerializedName("IsSycotaxAvailable")
        var isSycotaxAvailable: Boolean?= false

)