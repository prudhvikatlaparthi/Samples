package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class VUCRMTypeOfOperators(
        @SerializedName("OperatorType")
        var operatorType: String? = "",
        @SerializedName("OperatorTypeCode")
        var operatorTypeCode: String? = "",
        @SerializedName("PricingRule")
        var pricingRule: String? = "",
        @SerializedName("BillingCycle")
        var billingCycle: String? = "",
        @SerializedName("OperatorTypeID")
        var operatorTypeId: Int? = 0,
        @SerializedName("BillingCycleID")
        var billingCycleId: Int? = 0,
        @SerializedName("PricingRuleID")
        var pricingRuleId: Int? = 0
) {
    override fun toString(): String {
        return "$operatorType"
    }
}