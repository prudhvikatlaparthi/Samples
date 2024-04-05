package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class PropertyVerificationPendingList(
        @SerializedName("PendingRequestList")
        var requestList: ArrayList<PendingRequestList> = arrayListOf()
)