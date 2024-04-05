package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class ADMParkingType(
        @SerializedName("ParkingType")
        var parkingType: String? = "",
        @SerializedName("ParkingTypeCode")
        var parkingTypeCode: String? = "",
        @SerializedName("ParkingTypeID")
        var parkingTypeID: Int,
        @SerializedName("PricingRuleID")
        var pricingRuleID: Int,
        @SerializedName("RateCycleID")
        var rateCycleID: Int,
        @SerializedName("RateCycle")
        var rateCycle: String? = "",
        @SerializedName("OverstayRateCycleID")
        var overstayRateCycleID: String,
        @SerializedName("OverstayPricingRuleID")
        var overstayPricingRuleID: String,
        @SerializedName("IsPass")
        var isPass: String? = null
) {
    override fun toString(): String {
        return parkingType ?: ""
    }
}