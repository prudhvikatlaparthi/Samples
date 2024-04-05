package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class CartTax(
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
        @SerializedName("AllowDelete")
        var allowDelete: String? = null,
        @SerializedName("Parcel")
        var parcelRes: String? = null,
        @Expose(serialize = false, deserialize = false)
        @Transient
        var attachment: ArrayList<COMDocumentReference>? = null

) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.createTypedArrayList(COMDocumentReference))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(cartID)
        parcel.writeValue(cartTypeID)
        parcel.writeString(cartSycoTaxID)
        parcel.writeString(cartNo)
        parcel.writeString(registrationDate)
        parcel.writeValue(accountID)
        parcel.writeString(active)
        parcel.writeString(accountName)
        parcel.writeString(cartType)
        parcel.writeString(productCode)
        parcel.writeString(owner)
        parcel.writeString(email)
        parcel.writeString(accountPhone)
        parcel.writeString(estimatedTax)
        parcel.writeValue(isInvoiceGenerated)
        parcel.writeString(country)
        parcel.writeString(state)
        parcel.writeString(city)
        parcel.writeString(zone)
        parcel.writeString(sector)
        parcel.writeString(street)
        parcel.writeString(zipCode)
        parcel.writeString(section)
        parcel.writeString(lot)
        parcel.writeString(allowDelete)
        parcel.writeString(parcelRes)
        parcel.writeTypedList(attachment)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CartTax> {
        override fun createFromParcel(parcel: Parcel): CartTax {
            return CartTax(parcel)
        }

        override fun newArray(size: Int): Array<CartTax?> {
            return arrayOfNulls(size)
        }
    }

}