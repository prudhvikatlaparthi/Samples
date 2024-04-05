package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.BusinessOwnership
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.model.COMNotes
import com.sgs.citytax.model.GeoAddress
import com.sgs.citytax.util.Constant

data class StoreCustomerB2C(
        var context: SecurityContext? = SecurityContext(),
        @SerializedName("contact")
        var businessOwnership: BusinessOwnership? = null,
        @SerializedName("acctypecode")
        var accountTypeCode: Constant.AccountTypeCode = Constant.AccountTypeCode.CUS,
        @SerializedName("attach")
        var attachment: List<COMDocumentReference>? = arrayListOf(),
        @SerializedName("notes")
        var note: List<COMNotes>? = arrayListOf(),
        @SerializedName("add")
        var geoAddress: GeoAddress? = null
)