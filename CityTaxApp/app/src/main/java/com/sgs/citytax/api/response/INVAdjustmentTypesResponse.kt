package com.sgs.citytax.api.response


import com.google.gson.annotations.SerializedName

data class INVAdjustmentTypesResponse(
    @SerializedName("INV_AdjustmentTypes")
    val iNVAdjustmentTypes: List<INVAdjustmentType>? = null
)