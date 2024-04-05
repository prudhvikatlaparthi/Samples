package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class PropertyComfortLevel(
        @SerializedName("ComfortLevel")
        var comfortLevel: String? = "",
        @SerializedName("ComfortLevelCode")
        var comfortLevelCode: String? = "",
        @SerializedName("ComfortLevelID")
        var comfortLevelID: Int? = 0,
        @SerializedName("PricingRuleID")
        var pricingRuleID: Int? = 0
) {
    override fun toString(): String {
        return comfortLevel.toString()
    }
}