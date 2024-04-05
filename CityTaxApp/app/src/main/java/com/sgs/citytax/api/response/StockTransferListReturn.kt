package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.AdjustmentsListResults
import com.sgs.citytax.model.StockTransferListResults
import java.math.BigDecimal

data class StockTransferListReturn(
    @SerializedName("TotalRecordsFound")
    var totalRecordsFound: Int? = 0,
    @SerializedName("SearchResults")
    var stockTransferListResults: ArrayList<StockTransferListResults>? = arrayListOf(),
)
