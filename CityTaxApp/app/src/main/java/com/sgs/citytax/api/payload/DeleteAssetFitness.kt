package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class DeleteAssetFitness(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("FitnessID")
        var primaryKeyValue: Int? = 0
)