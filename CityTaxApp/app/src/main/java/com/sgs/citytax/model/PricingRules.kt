package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class PricingRules(
        @SerializedName("PricingRule")
        var pricingRule: String? = null,
        @SerializedName("PricingRuleID")
        var pricingRuleID: Int? = 0

) {
    override fun toString(): String {
        return "$pricingRule"
    }
}