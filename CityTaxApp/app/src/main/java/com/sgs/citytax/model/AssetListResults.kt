package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class AssetListResults(
        @SerializedName("AssetList4Return")
        var assetsLists:ArrayList<AssetListForReturn> = arrayListOf()
)