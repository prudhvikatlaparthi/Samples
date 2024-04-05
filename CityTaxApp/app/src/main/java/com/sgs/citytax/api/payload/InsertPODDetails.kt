package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName

/*PublicDomainOccupancyID
OrganizationID
OccupancyID
TaxableMatter
TaxPeriod
Active
Description
Length
Height
Width
]*/
data class InsertPODDetails(
        @SerializedName("PublicDomainOccupancyID")
        var publicDomainOccupancyID: String? = null,
        @SerializedName("orgzid")
        var organizationID: String? = null,
        @SerializedName("OccupancyID")
        var occupancyID: String? = null,
        @SerializedName("TaxableMatter")
        var taxableMatter: String? = null,
        @SerializedName("taxPeriod")
        var taxPeriod: String? = null,
        @SerializedName("Active")
        var active: String? = null,
        @SerializedName("Description")
        var description: String? = null,
        @SerializedName("Length")
        var length: String? = null,
        @SerializedName("Height")
        var height: String? = null,
        @SerializedName("Width")
        var width: String? = null,
        @SerializedName("IFU")
        var IFU: String? = null,
        @SerializedName("BusinessName")
        var businessName: String? = null,
        @SerializedName("strtdt")
        var startDate: String? = null

)
