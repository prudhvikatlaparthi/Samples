package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class PropertyVerificationRequestData(
        @SerializedName("PropertyVerificationRequestID")
        var verificationRequestId: Int? = 0,
        @SerializedName("PropertyID")
        var propertyId: Int? = 0,
        @SerializedName("AllowDocumentVerification")
        var allowDocumentVerification: String? = null,
        @SerializedName("AllowPhysicalVerification")
        var allowPhysicalVerification: String? = null,
        @SerializedName("DocumentVerificationRemarks")
        var documentRemarks: String? = null,
        @SerializedName("PhysicalVerificationRemarks")
        var physicalRemarks: String? =  null
)