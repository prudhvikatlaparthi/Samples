package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.AdjustmentsListResults

data class AdjustmentListReturn(
    @SerializedName("TotalRecordsFound")
    var totalRecordsFound: Int? = 0,
    @SerializedName("SearchResults")
    var adjustmentsListResults: ArrayList<AdjustmentsListResults>? = arrayListOf(),

    )