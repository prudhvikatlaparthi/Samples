package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.PropertyTaxNoticeModel
import com.sgs.citytax.model.PropertyTaxNoticeOwnerShipModel
import com.sgs.citytax.model.PropertyTaxNoticePropertyDetailsModel

data class PropertyTaxReceiptResponse(
        @SerializedName("Table")
        var table: ArrayList<PropertyTaxNoticeModel> = arrayListOf(),
        @SerializedName("Table1")
        var table1: ArrayList<PropertyTaxNoticeOwnerShipModel> = arrayListOf(),
        @SerializedName("Table2")
        var table2: ArrayList<PropertyTaxNoticePropertyDetailsModel?> = arrayListOf(),
        @SerializedName("OrgData")
        val orgData: List<OrgData>? = null
)