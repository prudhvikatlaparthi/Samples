package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class GetLAWTaxTransactionsList(
        @SerializedName("Table")
        var results: List<ImpondmentReturn> = arrayListOf()
)