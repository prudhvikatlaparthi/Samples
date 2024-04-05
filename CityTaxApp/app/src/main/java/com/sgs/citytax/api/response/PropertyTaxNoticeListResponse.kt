package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.TaxNoticeHistoryList

data class PropertyTaxNoticeListResponse(
        @SerializedName("PropertyTaxNoticeHistory")
        var propertyTaxNoticeHistory: List<TaxNoticeHistoryList>? = null
)
