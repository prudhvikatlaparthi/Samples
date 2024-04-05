package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Weapon(
        @SerializedName("WeaponID")
        var weaponID: Int? = 0,
        @SerializedName("WeaponTypeID")
        var weaponTypeID: Int? = 0,
        @SerializedName("WeaponSycotaxID")
        var weaponSycotaxID: String? = null,
        @SerializedName("serno", alternate = ["SerialNo"])
        var serialNo: String? = null,
        @SerializedName("make", alternate = ["Make"])
        var make: String? = "",
        @SerializedName("Model")
        var model: String? = "",
        @SerializedName("RegistrationDate")
        var registrationDate: String? = "",
        @SerializedName("PurposeOfPossession")
        var purposeOfPossession: String? = "",
        @SerializedName("desc", alternate = ["Description"])
        var description: String? = "",
        @SerializedName("acctid", alternate = ["AccountID"])
        var accountID: Int = 0,
        @SerializedName("act", alternate = ["Active"])
        var active: String? = "N",
        @SerializedName("AccountName")
        var accountName: String? = null,
        @SerializedName("WeaponType")
        var weaponType: String? = "",
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
        var country: String? = "",
        @SerializedName("State")
        var state: String? = "",
        @SerializedName("City")
        var city: String? = "",
        @SerializedName("Zone")
        var zone: String? = "",
        @SerializedName("Sector")
        var sector: String? = "",
        @SerializedName("Street")
        var street: String? = "",
        @SerializedName("ZipCode")
        var zipCode: String? = "",
        @SerializedName("Section")
        var section: String? = "",
        @SerializedName("Lot")
        var lot: String? = "",
        @SerializedName("AllowDelete")
        var allowDelete: String? = "",
        @SerializedName("Parcel")
        var parcelRes: String? = "",
        @Expose(serialize = false, deserialize = false)
        @Transient
        var attachment: ArrayList<COMDocumentReference>? = null
//        @SerializedName("Latitude")
//        var latitude: String? = null,
//        @SerializedName("Longitude")
//        var longitude: String? = null

) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
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
        parcel.writeValue(weaponID)
        parcel.writeValue(weaponTypeID)
        parcel.writeString(weaponSycotaxID)
        parcel.writeString(serialNo)
        parcel.writeString(make)
        parcel.writeString(model)
        parcel.writeString(registrationDate)
        parcel.writeString(purposeOfPossession)
        parcel.writeString(description)
        parcel.writeInt(accountID)
        parcel.writeString(active)
        parcel.writeString(accountName)
        parcel.writeString(weaponType)
        parcel.writeString(weaponTypeCode)
        parcel.writeString(productCode)
        parcel.writeString(owner)
        parcel.writeString(email)
        parcel.writeInt(weaponExemptionReasonID)
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

    companion object CREATOR : Parcelable.Creator<Weapon> {
        override fun createFromParcel(parcel: Parcel): Weapon {
            return Weapon(parcel)
        }

        override fun newArray(size: Int): Array<Weapon?> {
            return arrayOfNulls(size)
        }
    }


}