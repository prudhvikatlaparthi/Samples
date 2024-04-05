package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class CartTaxSummary(
        @SerializedName("CartID")
        var cartID: Int? = 0,
        @SerializedName("CartTypeID")
        var cartTypeID: Int? = 0,
        @SerializedName("CartSycotaxID")
        var cartSycoTaxID: String? = "",
        @SerializedName("CartNo")
        var cartNo: String? = "",
        @SerializedName("RegistrationDate")
        var registrationDate: String? = "",
        @SerializedName("acctid", alternate = ["AccountID"])
        var accountID: Int? = 0,
        @SerializedName("act", alternate = ["Active"])
        var active: String? = "N",
        @SerializedName("AccountName")
        var accountName: String? = "",
        @SerializedName("CartType")
        var cartType: String? = "",
        @SerializedName("ProductCode")
        var productCode: String? = "",
        @SerializedName("Owner")
        var owner: String? = "",
        @SerializedName("Email")
        var email: String? = "",
        @SerializedName("Number")
        var accountPhone: String? = "",
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

)