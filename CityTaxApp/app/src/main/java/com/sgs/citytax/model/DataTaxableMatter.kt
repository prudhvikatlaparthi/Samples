package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class DataTaxableMatter(
        @SerializedName("TaxableMatterColumnName")
        var taxableMatterColumnName: String? = "",
        @SerializedName("TaxableMatter")
        var taxableMatter: String? = null
)