package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.model.COMNotes
import com.sgs.citytax.model.GamingMachineTax

data class StoreGamingMachinesTax(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("gamingmachines")
        var gamingmachines: GamingMachineTax? = null,
        @SerializedName("attach")
        var attachment: List<COMDocumentReference>? = arrayListOf(),
        @SerializedName("notes")
        var note: List<COMNotes>? = arrayListOf()
)