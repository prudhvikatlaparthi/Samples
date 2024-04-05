package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.model.INVStockAllocation

data class StoreStockTransferPayload (
    @SerializedName("context")
    var context : SecurityContext = SecurityContext(),
    @SerializedName("allocation")
    var stockAllocation : List<INVStockAllocation> = arrayListOf(),
    @SerializedName("is_andr")
    var isAndroid: Boolean = false,
    @SerializedName("attach")
    var documentsList: ArrayList<COMDocumentReference> = arrayListOf()
)