package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class GetAssetInsuranceTypes(
        @SerializedName("AST_AssetInsuranceTypes")
        val assetInsuranceTypes: ArrayList<GetInsuranceTypes> = arrayListOf()
)