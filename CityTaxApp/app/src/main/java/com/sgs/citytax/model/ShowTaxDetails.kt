package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class ShowTaxDetails(
        @SerializedName("ShowsDetails")
        var showDetails: ArrayList<ShowsDetailsTable>? = null
)