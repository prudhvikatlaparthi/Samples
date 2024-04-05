package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.payload.AssetDetails
import com.sgs.citytax.api.payload.AssetSycoTaxId


data class AssetSycoTaxIdList(
        @SerializedName("SycotaxList")
        var sycotaxList: List<AssetSycoTaxId> = arrayListOf()

)