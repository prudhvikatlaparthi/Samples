package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName

class PropertyOwnerNomineePayload(
        @SerializedName("propertyOwnershipID")
        var propertyOwnershipID: Int? = 0,
        @SerializedName("4rmdt")
        var fromDate: String? = "",
        @SerializedName("2dt")
        var toDate: String? = null,
        @SerializedName("regno")
        var registrationNo: String? = "",
        @SerializedName("PropertyExemptionReasonID")
        var propertyExemptionReasonID: Int? = null,
        @SerializedName("PropertyOwnerID")
        var propertyOwnerID: Int? = null,
        @SerializedName("OwnerAccountID")
        var ownerAccountID: Int? = null,
        @SerializedName("RelationshipType")
        var relationshipType: String? = "",
        @SerializedName("NomineeAccountID")
        var nomineeAccountID: Int? = null

)