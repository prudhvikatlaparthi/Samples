package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName


data class AssetSycoTaxIdBySearch(
        @SerializedName("Results")
        var results: AssetSycoTaxIdList?= null
)