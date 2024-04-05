package com.sgs.citytax.model

import com.sgs.citytax.api.payload.AccountPhone

class ObjBusinessOwner {
    var businessOwnership = BusinessOwnership()
    var documents: ArrayList<COMDocumentReference> = arrayListOf()
    var notes: ArrayList<COMNotes> = arrayListOf()
    var accountEmails: ArrayList<CRMAccountEmails> = arrayListOf()
    var accountPhones: ArrayList<AccountPhone> = arrayListOf()
    var addresses: ArrayList<GeoAddress> = arrayListOf()
}