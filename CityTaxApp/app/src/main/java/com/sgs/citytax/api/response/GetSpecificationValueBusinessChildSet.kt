package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class GetSpecificationValueBusinessChildSet(
        @SerializedName("Results")
        var results: GetSpecificationValueBusinessChildSets? = null,
        @SerializedName("TotalSearchedRecords")
        var totalSearchedRecords: Int = 0
)