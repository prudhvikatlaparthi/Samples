package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.TaskCode

data class StoreDueAgreementResponse(
        @SerializedName("DueAgreementID")
        var dueAgreementID: Int = 0,
        @SerializedName("ReferenceNo")
        var referenceNo: String? = ""
)