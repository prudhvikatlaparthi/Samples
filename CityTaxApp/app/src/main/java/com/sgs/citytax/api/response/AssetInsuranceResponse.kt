package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class AssetInsuranceResponse(
        @SerializedName("VU_AST_AssetInsurances")
        var assetInsurances: ArrayList<AssetInsuranceData> = arrayListOf()
)