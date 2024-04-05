package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class Tenure(
        @SerializedName("TenurePeriod")
        var tenurePeriod: Int? = 0
) {
    override fun toString(): String {
        return "$tenurePeriod"
    }
}