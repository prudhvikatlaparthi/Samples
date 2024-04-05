package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName

class PropertyOwnershipPayload(
        @SerializedName("propertyOwnershipID")
        var propertyOwnershipID: Int? = null,
        @SerializedName("proprtyid", alternate = ["propertyID"])
        var propertyID: Int? = null,
        @SerializedName("4rmdt", alternate = ["fromDate"])
        var fromDate: String? = "",
        @SerializedName("2dt", alternate = ["toDate"])
        var toDate: String? = null,
        @SerializedName("regno", alternate = ["registrationNo"])
        var registrationNo: String? = "",
        @SerializedName("PropertyExemptionReasonID")
        var propertyExemptionReasonID: Int? = null
)