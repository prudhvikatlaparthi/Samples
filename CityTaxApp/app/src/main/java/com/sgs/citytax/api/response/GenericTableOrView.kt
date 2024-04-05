package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.CRMActivityClass
import com.sgs.citytax.model.VUCOMPropertyVerificationRequests
import com.sgs.citytax.model.VuComProperties
import com.sgs.citytax.model.VuInvProducts

class GenericTableOrView(
        @SerializedName("COM_RoundingMethods")
        var roundingMethods: List<RoundingMethod>? = null,
        @SerializedName("VU_CRM_IndividualTaxes")
        var individualTax: List<VuCrmIndividualTaxes>? = null,
        @SerializedName("VU_INV_Products")
        var products: ArrayList<VuInvProducts>? = null,
        @SerializedName("VU_COM_PROPERTIES", alternate = ["VU_COM_LANDS"])
        var property: List<VuComProperties>? = null,
        @SerializedName("VU_COM_PropertyVerificationRequests")
        var propertyVerifications: List<VUCOMPropertyVerificationRequests>? = null,
        @SerializedName("VU_COM_PropertyMaster")
        var propertyMaster: ArrayList<VuComPropertyMaster>? = null,
        @SerializedName("CRM_ActivityClasses")
        var activityClass: MutableList<CRMActivityClass>? = null
)