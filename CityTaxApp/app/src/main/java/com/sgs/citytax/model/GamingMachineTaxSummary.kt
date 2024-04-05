package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class GamingMachineTaxSummary(
        @SerializedName("GamingMachineID")
        var gamingMachineID: Int? = null,
        @SerializedName("GamingMachineTypeID")
        var gamingMachineTypeID: Int? = null,
        @SerializedName("GamingMachineSycotaxID")
        var gamingMachineSycotaxID: String? = null,
        @SerializedName("RegistrationDate")
        var registrationDate: String? = null,
        @SerializedName("acctid", alternate = ["AccountID"])
        var accountID: Int? = null,
        @SerializedName("lat", alternate = ["Latitude"])
        var latitude: String? = null,
        @SerializedName("long", alternate = ["Longitude"])
        var longitude: String? = null,
        @SerializedName("act", alternate = ["Active"])
        var active: String? = null,
        @SerializedName("serno", alternate = ["SerialNo"])
        var serialNo: String? = null,
        @SerializedName("Number")
        var accountPhone: String? = "",
        @SerializedName("AccountName")
        var accountName: String? = "",
        @SerializedName("GamingMachineType")
        var gamingMachineType: String? = "",
        @SerializedName("ProductCode")
        var productCode: String? = "",
        @SerializedName("Owner")
        var owner: String? = "",
        @SerializedName("Email")
        var email: String? = "",
        @SerializedName("CompleteAddress")
        var completeAddress: String? = "",
        @SerializedName("EstimatedTax")
        var estimatedTax: String? = "",
        @SerializedName("IsInvoiceGenerated")
        var isInvoiceGenerated: Boolean? = false,
        @SerializedName("Country")
        var country: String? = null,
        @SerializedName("State")
        var state: String? = null,
        @SerializedName("City")
        var city: String? = null,
        @SerializedName("Zone")
        var zone: String? = null,
        @SerializedName("Sector")
        var sector: String? = null,
        @SerializedName("Street")
        var street: String? = null,
        @SerializedName("ZipCode")
        var zipCode: String? = null,
        @SerializedName("Section")
        var section: String? = null,
        @SerializedName("Lot")
        var lot: String? = null,
        @SerializedName("Parcel")
        var parcelRes: String? = null,
        @SerializedName("DocDetails")
        var documentDetails: DocDetails?  = null
)