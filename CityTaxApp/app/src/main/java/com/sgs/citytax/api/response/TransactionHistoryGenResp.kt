package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.TransactionHistoryGenModel

data class TransactionHistoryGenResp(

        @SerializedName("Results")
        var transactions: ArrayList<TransactionHistoryGenModel> = arrayListOf(),
        @SerializedName("PageSize")
        var pageSize: Int? = 10,
        @SerializedName("PageIndex")
        var pageIndex: Int? = 1,
        @SerializedName("TotalRecords")
        var totalRecords: Int? = 0
)
