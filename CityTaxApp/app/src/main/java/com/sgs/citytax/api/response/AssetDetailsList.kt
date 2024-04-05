package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.payload.AssetDetails


data class AssetDetailsList(
        @SerializedName("AssetList")
        var assetList: List<AssetDetails> = arrayListOf()

)