package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class SubscriptionModel(
        @SerializedName("ModelName")
        var modelName: String? = null,
        @SerializedName("PaymentCycle")
        var paymentCycle: String? = null,
        @SerializedName("PricingRule")
        var pricingRule: String? = null,
        @SerializedName("PaymentCycleID")
        var paymentCycleID: Int? = 0,
        @SerializedName("PricingRuleID")
        var pricingRuleID: Int? = 0,
        @SerializedName("SubscriptionModelID")
        var subscriptionModelID: Int? = 0
) {
    override fun toString(): String {
        return "$modelName"
    }
}