package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class ListDueNoticeResponse(
    @SerializedName("TotalRecordsFound")
    var totalRecords:Int?=null,
    @SerializedName("SearchResults")
    var results:List<ListDueNoticeResult>?= null
    )