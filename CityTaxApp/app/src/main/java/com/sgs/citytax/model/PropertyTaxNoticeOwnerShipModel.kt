package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class PropertyTaxNoticeOwnerShipModel(
        @SerializedName("proprtyid")
        var proprtyid: Int? = null,
        @SerializedName("PropertyOwnershipID")
        var PropertyOwnershipID: Int? = null,
        @SerializedName("PropertyExemptionReasonID")
        var PropertyExemptionReasonID: Int? = null,
        @SerializedName("acctid")
        var acctid: String? = "",
        @SerializedName("acctname")
        var acctname: String = "",
        @SerializedName("email")
        var email: String? = "",
        @SerializedName("mob")
        var mob: String = "",
        @SerializedName("SycotaxID")
        var SycotaxID: String? = "",
        @SerializedName("CitizenID")
        var CitizenID: String? = "",
        @SerializedName("MobileWithCode")
        var MobileWithCode: String? = "",
        @SerializedName("PropertyOwnerIDSycoTax")
        var propertyOwnerIDSycoTax: String? = "",
        @SerializedName("IDCardNumbers")
        var citizenCardNumber: String? = ""

)