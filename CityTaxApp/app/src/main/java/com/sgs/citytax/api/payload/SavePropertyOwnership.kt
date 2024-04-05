package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName

data class SavePropertyOwnership(
        @SerializedName("PropertyOwnershipID")
        var propertyOwnershipID: Int? = 0,
        @SerializedName("regno")
        var registrationNo: String? = "",
        @SerializedName("4rmdt")
        var fromDate: String? = "",
        @SerializedName("2dt")
        var toDate: String? = "",
        @SerializedName("acctid")
        var accountID: Int? = 0
)