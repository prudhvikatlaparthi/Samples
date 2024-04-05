package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.PendingLicenses4Agent

data class SearchPendingLicensesList(
        @SerializedName("PendingLicenses")
        var pendingLicenses: List<PendingLicenses4Agent>? = null
)