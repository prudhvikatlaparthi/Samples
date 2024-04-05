package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class LicenseEstimatedTax(
        @SerializedName("acctid")
        var accountId: Int? = 0,
        @com.google.gson.annotations.SerializedName("ctyid")
        var cityId: Int? = 0,
        @SerializedName("strtdt")
        var startDate: String? = "",
        @SerializedName("LicenseCategoryID")
        var licenseCategoryId: Int? = 0
)