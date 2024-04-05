package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.AssetRentalSpecificationsList
import com.sgs.citytax.model.AssetSpecifications

data class AssetPrePostResponse(
        @SerializedName("PreCheckList")
        var assetRentalSpecifications:ArrayList<AssetRentalSpecificationsList> = arrayListOf(),
        @SerializedName("PostCheckList")
        var assetSpecifications:List<AssetSpecs> = arrayListOf()
)