package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class SearchPendingLicensesResponse(
        @SerializedName("Results")
        var PendingLicenses4Agent: SearchPendingLicensesList? = null,
        @SerializedName("TotalSearchedRecords")
        var totalSearchedRecords: Int? = 0
)