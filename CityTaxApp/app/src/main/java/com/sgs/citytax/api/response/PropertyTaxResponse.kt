package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.payload.VerificationDetails
import com.sgs.citytax.model.COMPropertyOwner
import com.sgs.citytax.model.GeoAddress
import com.sgs.citytax.model.PropertyTax

data class PropertyTaxResponse(
        @SerializedName("Property")
        var propertyTax: ArrayList<PropertyTax> = arrayListOf(),
        @SerializedName("Address")
        var address:ArrayList<GeoAddress> = arrayListOf(),
        @SerializedName("Owners")
        var propertyOwners:ArrayList<COMPropertyOwner> = arrayListOf(),
        @SerializedName("VerificationDetails")
        var verificationDetails:ArrayList<VerificationDetails> = arrayListOf()
)