package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class ImpoundmentLineSummary(
        @SerializedName("ReturnLineID")
        var returnLineID: Int? = 0,
        @SerializedName("ImpoundmentID")
        var impoundmentID: Int? = 0,
        @SerializedName("ReturnDate")
        var impoundmentReturnDate: String? = "",
        @SerializedName("qty")
        var quantity: BigDecimal = BigDecimal.ZERO,
        @SerializedName("amt")
        var amount: BigDecimal = BigDecimal.ZERO,
        @SerializedName("HandoverImageID")
        var imageId: Int? = 0,
        @SerializedName("OwnerSignatureID")
        var ownerSignatureId: Int? = 0,
        @SerializedName("ReturnAgentSignatureID")
        var returnSigId: Int? = 0,
        @SerializedName("ReturnRemarks")
        var returnRemarks: String? = "",
        @SerializedName("HandoverImageAWSPath")
        var handoverImageAWSPath: String? = "",
        @SerializedName("OwnerSignatureAWSPath")
        var ownerSignatureAWSPath: String? = "",
        @SerializedName("ReturnAgentSignatureAWSPath")
        var customerSignatureAWSPath: String? = "" ,
        @SerializedName("FineAmount")
        var fineAmount: Double? = 0.0,
        @SerializedName("ImpoundCharge")
        var impoundmentCharge: Double? = 0.0,
        @SerializedName("ViolationCharge")
        var violationCharge: Double? = 0.0
)