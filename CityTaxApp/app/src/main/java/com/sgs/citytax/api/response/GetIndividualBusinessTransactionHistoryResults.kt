package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class GetIndividualBusinessTransactionHistoryResults(
        @SerializedName("Results")
        var businessTransactionHistoryList: ArrayList<BusinessTransaction> = arrayListOf()
)