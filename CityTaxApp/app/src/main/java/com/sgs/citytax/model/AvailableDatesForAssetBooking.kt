package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.util.formatDate

data class AvailableDatesForAssetBooking(
        @SerializedName("dt")
        var date: String? = null,
        @SerializedName("Stock")
        var stock: Int? = 0
) {
    override fun toString(): String {
        return formatDate(date)
    }
}