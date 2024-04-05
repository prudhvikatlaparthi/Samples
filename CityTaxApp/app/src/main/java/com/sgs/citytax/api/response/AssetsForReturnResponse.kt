package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.AssetListForReturn
import com.sgs.citytax.model.AssetListResults

data class AssetsForReturnResponse(
        @SerializedName("Results")
        var assetsListForResult:AssetListResults?=null
)