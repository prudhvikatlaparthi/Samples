package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class AssetFitnessesResponse(
        @SerializedName("VU_AST_AssetFitnesses")
        var assetFitnessesData: ArrayList<AssetFitnessesData> = arrayListOf()
)