package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class SubscriptionResponse(
        @SerializedName("acctid")
        var acctid: String? = "",
        @SerializedName("usrid")
        var usrid: String? = "",
        @SerializedName("LicenceKey")
        var LicenceKey: String? = "",
        @SerializedName("LastRenewalDate")
        var LastRenewalDate: String? = "",
        @SerializedName("ValidFromDate")
        var ValidFromDate: String? = "",
        @SerializedName("ValidUptoDate")
        var ValidUptoDate: String? = "",
        @SerializedName("RemainingDays")
        var RemainingDays: String? = ""
)