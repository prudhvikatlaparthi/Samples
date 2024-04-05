package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GamingMachineTax(
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
            parcel.readValue(Int::class.java.classLoader) as? Int,
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
            parcel.createTypedArrayList (COMDocumentReference))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(gamingMachineID)
        parcel.writeValue(gamingMachineTypeID)
        parcel.writeString(gamingMachineSycotaxID)
        parcel.writeString(registrationDate)
        parcel.writeValue(accountID)
        parcel.writeString(latitude)
        parcel.writeString(longitude)
        parcel.writeString(active)
        parcel.writeString(serialNo)
        parcel.writeString(accountPhone)
        parcel.writeString(accountName)
        parcel.writeString(gamingMachineType)
        parcel.writeString(productCode)
        parcel.writeString(owner)
        parcel.writeString(email)
        parcel.writeString(completeAddress)
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

    companion object CREATOR : Parcelable.Creator<GamingMachineTax> {
        override fun createFromParcel(parcel: Parcel): GamingMachineTax {
            return GamingMachineTax(parcel)
        }

        override fun newArray(size: Int): Array<GamingMachineTax?> {
            return arrayOfNulls(size)
        }
    }

}