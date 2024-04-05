package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class VUCRMStarOfHotels(
        @SerializedName("Star")
        var star: String? = "",
        @SerializedName("StarCode")
        var starCode: String? = "",
        @SerializedName("PricingRule")
        var pricingRule: String? = "",
        @SerializedName("BillingCycle")
        var billingCycle: String? = "",
        @SerializedName("StarID")
        var starID: Int? = 0,
        @SerializedName("BillingCycleID")
        var billingCycleId: Int? = 0,
        @SerializedName("PricingRuleID")
        var pricingRuleID: Int? = 0
){
    override fun toString(): String {
        return "$star"
    }
}