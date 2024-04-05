package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.ShowTaxDetails

data class ShowTaxListResponse(
        @SerializedName("Results")
        var taxDetails: ShowTaxDetails?= null
)