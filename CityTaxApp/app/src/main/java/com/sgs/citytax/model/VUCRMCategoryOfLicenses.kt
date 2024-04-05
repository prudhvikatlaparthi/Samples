package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class VUCRMCategoryOfLicenses(
        @SerializedName("LicenseCategoryID")
        var licenseCategoryID: Int? = 0,
        @SerializedName("LicenseCategory")
        var licenseCategory: String? = "",
        @SerializedName("LicenseCategoryCode")
        var licenseCategoryCode: String? = "",
        @SerializedName("BillingCycleID")
        var billingCycleID: Int? = 0,
        @SerializedName("PricingRuleID")
        var pricingRuleId: Int? = 0,
        @SerializedName("act")
        var active: String? = "",
        @SerializedName("AuthorizedBeverages")
        var authorisedBevarages: String? = ""
){
        override fun toString(): String {
                return "$licenseCategory"
        }
}