package com.sgs.citytax.api.response


import com.google.gson.annotations.SerializedName

data class INVAdjustmentType(
    @SerializedName("AdjustmentType")
    val adjustmentType: String? = null,
    @SerializedName("AdjustmentTypeCode")
    val adjustmentTypeCode: String? = null,
    @SerializedName("AdjustmentTypeID")
    val adjustmentTypeID: Int? = null,
    @SerializedName("StockInOut")
    val stockInOut: String? = null
){
    override fun toString(): String {
        return "$adjustmentType\n$stockInOut"
    }
}