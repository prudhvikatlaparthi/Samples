package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class CRMServiceSubType(
        @SerializedName("ServiceSubType")
        var serviceSubType: String? = "",
        @SerializedName("ServiceSubTypeID")
        var serviceSubTypeID: Int? = 0,
        @SerializedName("ServiceTypeID")
        var serviceTypeID: Int? = 0,
        @SerializedName("PricingRuleID")
        var pricingRuleID: Int? = 0,
        @SerializedName("act")
        var isActive: String? = "",
        @SerializedName("unitcode")
        var unitCode: String? = "",
        @SerializedName("AdvanceAmount")
        var advanceAmount: BigDecimal? = BigDecimal.ZERO
) {
    override fun toString(): String {
        return serviceSubType.toString()
    }
}