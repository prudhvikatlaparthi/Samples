package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class DocDetails(
        @SerializedName("COM_DocumentReference")
        var comDocumentReferences: List<COMDocumentReference> = arrayListOf()

)
