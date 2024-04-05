package com.sgs.citytax.api.response


import com.google.gson.annotations.SerializedName


data class GetDropdownFiltersForBusinessSearchResponse(
        @SerializedName("COM_ZoneMaster")
        var cOMZoneMaster: List<COMZoneMasterS>? = null,
        @SerializedName("COM_Sectors")
        var cOMSectors: List<COMSector>? = null,
        @SerializedName("VU_CRM_TaxSubTypes")
        var vUCRMTaxSubTypes: List<VUCRMTaxSubType>? = null,
        @SerializedName("CRM_ActivityDomains")
        var cRMActivityDomains: ArrayList<CRMActivityDomainS>? = null,
        @SerializedName("CRM_ActivityClasses")
        var cRMActivityClasses: List<CRMActivityClassS>? = null,
        @SerializedName("VU_INV_Products")
        var vUINVProducts: List<VUINVProducts>? = null
        )


