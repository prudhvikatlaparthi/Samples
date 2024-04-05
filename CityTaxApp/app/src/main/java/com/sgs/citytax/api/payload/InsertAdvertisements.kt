package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.model.CRMAdvertisements

data class InsertAdvertisements(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("advertisements")
        var crmAdvertisements: CRMAdvertisements? = null,
        @SerializedName("attach")
        var comDocumentReference: ArrayList<COMDocumentReference> = arrayListOf()

)