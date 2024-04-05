package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.model.COMNotes
import com.sgs.citytax.model.CRMAgents

data class InsertAgent(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("agent")
        var crmAgents: CRMAgents? = null,
        @SerializedName("attach")
        var attachments: List<COMDocumentReference>? = null,
        @SerializedName("notes")
        var notes: List<COMNotes>? = null
)