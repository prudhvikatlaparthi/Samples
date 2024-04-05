package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class CRMAdvertisements(
        @SerializedName("AdvertisementID")
        var advertisementId: Int? = 0,
        @SerializedName("orgzid")
        var organisationId: Int? = 0,
        @SerializedName("AdvertisementTypeID")
        var advertisementTypeId: Int? = 0,
        @SerializedName("act")
        var active: String? = "",
        @SerializedName("desc")
        var description: String? = "",
        @SerializedName("qty")
        var quantity: Int? = 0,
        @SerializedName("strtdt")
        var startDate: String? = null,
        @SerializedName("Length")
        var Length: String? = null,
        @SerializedName("wdth")
        var wdth: String? = null,
        @SerializedName("unitcode")
        var unitcode: String? = null,
        @SerializedName("TaxableMatter")
        var TaxableMatter: String? = null
)