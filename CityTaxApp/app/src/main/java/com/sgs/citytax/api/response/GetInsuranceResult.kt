package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class GetInsuranceResult(
        @SerializedName("Results")
        var results: GetAssetInsuranceTypes? = null
)