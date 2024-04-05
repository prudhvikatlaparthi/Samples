package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.*

data class InsertMultipleViolationTicket(
        val context:SecurityContext = SecurityContext(),
        @SerializedName("multitypes")
        var multiViolationTypes:ArrayList<MultipleViolationTypes> = arrayListOf(),
        @SerializedName("data")
        var vehicleTicketData: VehicleTicketData? = null,
        @SerializedName("geoaddr")
        var geoAddress: GeoAddress? = null,
        @SerializedName("fileext")
        var documentExtension: String? = "",
        @SerializedName("fileData")
        var fileData: String? = "",
        @SerializedName("signatures")
        var signature: ViolationSignature?=null,
        @SerializedName("attach")
        var documentsList: ArrayList<COMDocumentReference> = arrayListOf()
)