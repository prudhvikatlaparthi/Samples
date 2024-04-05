package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName

class PropertyOwnersPayload(
        @SerializedName("propertyOwnerID")
        var propertyOwnerID: Int? = null,
        @SerializedName("propertyOwnershipID")
        var propertyOwnershipID: Int? = null,
        @SerializedName("ownerAccountID")
        var ownerAccountID: Int? = null,
        @SerializedName("relationshipType")
        var relationshipType: String? = "",
        @SerializedName("nomineeAccountID")
        var nomineeAccountID: Int? = null
)