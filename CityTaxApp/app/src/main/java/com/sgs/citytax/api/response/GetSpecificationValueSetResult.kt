package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class GetSpecificationValueSetResult(
        @SerializedName("Results")
        var results: GetSpecificationValueSets? = null
)