package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.CitizenSycoTaxID

data class CitizenSycoTaxResponse(
        @SerializedName("CitizenSycotaxList")
        var sycoTaxIDs: ArrayList<CitizenSycoTaxID>? = arrayListOf()
)