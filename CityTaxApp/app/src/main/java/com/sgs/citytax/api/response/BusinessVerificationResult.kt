package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.TaxPayerDetails

data class BusinessVerificationResult(
        @SerializedName("TaxPayerDetails")
        var results: List<TaxPayerDetails>? = arrayListOf()
)