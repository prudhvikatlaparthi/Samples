package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class COMPropertyTypes(
        @SerializedName("PropertyType")
        var propertyType: String? = "",
        @SerializedName("act")
        var active: String? = "",
        @SerializedName("PropertyTypeCode")
        var propertyTypeCode: String? = "",
        @SerializedName("PropertyTypeID")
        var propertyTypeID: Int? = 0,
        @SerializedName("prodcode")
        var productCode: String? = "",
        @SerializedName("prod")
        var product: String? = "",
        @SerializedName("TaxRuleBookCode")
        var taxRuleBookCode: String? = "",
        @SerializedName("BillingCycleID")
        var mBillingCycleID: String? = "",
        @SerializedName("RateCycle")
        var mRateCycle: String? = "",
        @SerializedName("PaymentCycle")
        var mPaymentCycle: String? = "",
        @SerializedName("unitcode")
        var unitcode: String? = "",
        @SerializedName("unit")
        var unit: String? = "",
        @SerializedName("IsApartment")
        var isApartment: String? = ""

) {
    override fun toString(): String {
        return propertyType.toString()
    }
}