package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.LawPenalties

data class GetLawPenaltyTransactionsResponse(
        @SerializedName("Table")
        var Penalties: ArrayList<LawPenalties> = arrayListOf()
)
