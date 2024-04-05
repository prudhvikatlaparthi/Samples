package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class LicensePlanElements(
        @SerializedName("usrid")
        var userId: String? = "",
        @SerializedName("UserName")
        var userName: String? = "",
        @SerializedName("LicenseCode")
        var licenseCode: String? = "",
        @SerializedName("ModelName")
        var modelName: String? = "",
        @SerializedName("4rmdt")
        var fromDate: String? = "",
        @SerializedName("2dt")
        var toDate: String? = "",
        @SerializedName("amt")
        var amount: Double? = 0.0
)