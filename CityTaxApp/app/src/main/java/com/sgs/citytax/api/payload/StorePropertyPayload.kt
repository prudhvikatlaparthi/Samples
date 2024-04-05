package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

class StorePropertyPayload (
        @SerializedName("context")
        var context: SecurityContext = SecurityContext(),
        @SerializedName("data")
        var data: StorePropertyData = StorePropertyData(),
        @SerializedName("ownershipdata")
        var ownershipdata: PropertyOwnerNomineePayload? =null

)