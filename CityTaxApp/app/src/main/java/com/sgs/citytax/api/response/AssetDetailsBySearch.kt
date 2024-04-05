package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName


data class AssetDetailsBySearch(
        @SerializedName("Results")
        var results: AssetDetailsList?= null
)