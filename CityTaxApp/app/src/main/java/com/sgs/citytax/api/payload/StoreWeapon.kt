package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.model.COMNotes
import com.sgs.citytax.model.Weapon

data class StoreWeapon(
        var context: SecurityContext? = SecurityContext(),
        @SerializedName("weapons")
        var weapons: Weapon? = null,
        @SerializedName("attach")
        var attachment: List<COMDocumentReference>? = arrayListOf(),
        @SerializedName("notes")
        var note: List<COMNotes>? = arrayListOf()
)