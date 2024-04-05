package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class GenericServiceResponse(
        @SerializedName("Results")
        var result: GenericTableOrView? = null,
        @SerializedName("PageSize")
        var pageSize: Int? = null,
        @SerializedName("PageIndex")
        var pageIndex: Int? = null,
        @SerializedName("RecordsFound")
        var recordsFound: Int? = null,
        @SerializedName("TotalRecords")
        var totalRecords: Int? = null,
        @SerializedName("TotalSearchedRecords")
        var totalSearchedRecords: Int = 0
)