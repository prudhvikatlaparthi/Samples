package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class GetTaskList(
        @SerializedName("Results")
        var results: TaskResult? = null,
        @SerializedName("TotalSearchedRecords")
        var totalSearchedRecords: Int = 0
)