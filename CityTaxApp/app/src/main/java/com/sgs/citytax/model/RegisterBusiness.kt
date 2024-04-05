package com.sgs.citytax.model

import com.sgs.citytax.api.payload.Organization

class RegisterBusiness {
    var sycoTaxID: String = ""
    var accountID: Int = 0
    var organizationID: Int = 0

    var vuCrmAccounts: VUCRMAccounts? = null
    var organization: Organization = Organization()
    var geoAddress: GeoAddress = GeoAddress()

    // region Get
    var documents: ArrayList<COMDocumentReference> = arrayListOf()
    var notes: ArrayList<COMNotes> = arrayListOf()
    // endregion

}