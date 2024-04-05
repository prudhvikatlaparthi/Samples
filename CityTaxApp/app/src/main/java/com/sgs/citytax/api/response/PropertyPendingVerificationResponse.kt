package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.PropertyVerificationPendingList

data class PropertyPendingVerificationResponse(
        @SerializedName("Results")
        var verificationList: PropertyVerificationPendingList? = null
)