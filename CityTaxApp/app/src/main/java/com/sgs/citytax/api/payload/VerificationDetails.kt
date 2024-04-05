package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName

data class VerificationDetails(
        @SerializedName("PropertyVerificationRequestID")
        var propertyVerificationRequestID: Int? = 0,
        @SerializedName("AllowDocumentVerification")
        var allowDocumentVerification: String? = "",
        @SerializedName("AllowPhysicalVerification")
        var allowPhysicalVerification: String? = "",
        @SerializedName("DocumentVerificatioDate")
        var documentVerificatioDate: String? = "",
        @SerializedName("PhysicalVerificatioDate")
        var physicalVerificatioDate: String? = "",
        @SerializedName("DocumentVerificationByUser")
        var documentVerificationByUser: String? = "",
        @SerializedName("PhysicalVerificationByUser")
        var physicalVerificationByUser: String? = ""
)