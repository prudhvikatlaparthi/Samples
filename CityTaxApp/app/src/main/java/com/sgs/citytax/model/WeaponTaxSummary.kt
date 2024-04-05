package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class WeaponTaxSummary(
        @SerializedName("WeaponID")
        var weaponID: Int? = 0,
        @SerializedName("WeaponTypeID")
        var weaponTypeID: Int? = 0,
        @SerializedName("WeaponSycotaxID")
        var weaponSycotaxID: String? = null,
        @SerializedName("serno", alternate = ["SerialNo"])
        var serialNo: String? = null,
        @SerializedName("make", alternate = ["Make"])
        var make: String? = null,
        @SerializedName("Model")
        var model: String? = null,
        @SerializedName("RegistrationDate")
        var registrationDate: String? = null,
        @SerializedName("PurposeOfPossession")
        var purposeOfPossession: String? = null,
        @SerializedName("desc", alternate = ["Description"])
        var description: String? = null,
        @SerializedName("acctid", alternate = ["AccountID"])
        var accountID: Int = 0,
        @SerializedName("act", alternate = ["Active"])
        var active: String? = "N",
        @SerializedName("AccountName")
        var accountName: String? = null,
        @SerializedName("WeaponType")
        var weaponType: String? = null,
        @SerializedName("WeaponTypeCode")
        var weaponTypeCode: String? = null,
        @SerializedName("ProductCode")
        var productCode: String? = null,
        @SerializedName("Owner")
        var owner: String? = null,
        @SerializedName("Email")
        var email: String? = null,
        @SerializedName("WeaponExemptionReasonID")
        var weaponExemptionReasonID: Int = 0,
        @SerializedName("Number")
        var accountPhone: String? = null,
        @SerializedName("EstimatedTax")
        var estimatedTax: String? = null,
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
//        @SerializedName("Latitude")
//        var latitude: String? = null,
//        @SerializedName("Longitude")
//        var longitude: String? = null

)

