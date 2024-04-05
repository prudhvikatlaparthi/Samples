package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.CPCarrierDeterminedTax
import com.sgs.citytax.model.CPCarrierPrapotaionalTax
import com.sgs.citytax.model.CPCarrierVariableTax
import com.sgs.citytax.model.CPTaxNoticeDetails

data class CPTaxNoticeResponse(
        @SerializedName("Table")
        var cpTaxNoticeDetails: ArrayList<CPTaxNoticeDetails> = arrayListOf(),
        @SerializedName("Table1")
        var determinedTaxes: ArrayList<CPCarrierDeterminedTax> = arrayListOf(),
        @SerializedName("Table2")
        var variableTaxes: ArrayList<CPCarrierVariableTax> = arrayListOf(),
        @SerializedName("Table3")
        var prapotionalTaxes: ArrayList<CPCarrierPrapotaionalTax> = arrayListOf(),
        @SerializedName("OrgData")
        val orgData: List<OrgData>? = null
)