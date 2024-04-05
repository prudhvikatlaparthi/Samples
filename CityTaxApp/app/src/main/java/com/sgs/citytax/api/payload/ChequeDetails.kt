package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class ChequeDetails(
        @SerializedName("orgBankAccountNo")
        var orgBankAccountNo : String ?= null,
        @SerializedName("bnkname")
        var bankName : String ?= null,
        @SerializedName("brname")
        var branchName : String ?= null,
        @SerializedName("chqno")
        var chequeNo : String ?= null,
        @SerializedName("chqdt")
        var chequeDate : String ?= null,
        @SerializedName("amt")
        var amount : BigDecimal ?= BigDecimal.ZERO,
        @SerializedName("chqid")
        var chequeID : Int ?= 0,
        @SerializedName("submissionDate")
        var submissionDate : String ?= null,
        @SerializedName("dpstdt")
        var depositDate : String ?= null,
        @SerializedName("clearanceDate")
        var clearanceDate : String ?= null,
        @SerializedName("stscode")
        var statusCode : String ?= null,
        @SerializedName("rmks")
        var remarks : String ?= null,
        @SerializedName("PenaltyPercentage")
        var penaltyPercentage : BigDecimal ?= null,
        @SerializedName("PenaltyAmount")
        var penaltyAmount : BigDecimal ?= null,
        @SerializedName("ProsecutionFees")
        var prosecutionFees : BigDecimal ?= null

)