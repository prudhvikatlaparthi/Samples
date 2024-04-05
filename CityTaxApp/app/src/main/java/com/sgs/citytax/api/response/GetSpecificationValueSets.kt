package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class GetSpecificationValueSets(
        @SerializedName("INV_SpecificationValueSets")
        var invSpecificationValueSets: ArrayList<AssetSpecsValueSets> = arrayListOf()
)