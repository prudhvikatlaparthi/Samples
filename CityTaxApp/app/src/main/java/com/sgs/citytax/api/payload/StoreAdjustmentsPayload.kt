package com.sgs.citytax.api.payload


import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.COMDocumentReference

data class StoreAdjustmentsPayload(
    var context: SecurityContext = SecurityContext(),
    @SerializedName("attach")
    var documentsList: ArrayList<COMDocumentReference> = arrayListOf(),
    @SerializedName("adjustments")
    var adjustments: List<AdjustmentItemPayload>? = null
)