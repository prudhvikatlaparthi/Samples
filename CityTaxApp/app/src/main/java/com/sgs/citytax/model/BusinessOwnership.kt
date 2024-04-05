package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BusinessOwnership(
        @SerializedName("stscode", alternate = ["StatusCode"])
        var statusCode: String? = "",
        @SerializedName("ProfessionID")
        var professionID: Int? = 0,
        @SerializedName("rmks", alternate = ["Remarks"])
        var remarks: String? = "",
        @SerializedName("frstname", alternate = ["FirstName"])
        var firstName: String? = "",
        @SerializedName("lastname", alternate = ["LastName"])
        var lastName: String? = "",
        @SerializedName("ContactName")
        var contactName: String? = "",
        @SerializedName("IFU")
        var ifu: String? = "",
        @SerializedName("TaxPayerAccountID")
        var taxPayerAccountID: Int? = 0,
        @SerializedName("conid", alternate = ["ContactID"])
        var contactID: Int? = 0,
        @SerializedName("Profession")
        var profession: String? = "",
        @SerializedName("acctid", alternate = ["AccountID"])
        var accountID: Int? = 0,
        @SerializedName("BusinessOwnerID")
        var businessOwnerID: String? = "",
        @SerializedName("CitizenID")
        var citizenID: String? = "",
        @SerializedName("OwnerAccountID")
        var ownerAccountID: Int? = 0,
        @SerializedName("AccountContactID")
        var accountContactID: Int? = 0,
        @SerializedName("ph", alternate = ["Number", "Phone", "Mobile"])
        var phone: String? = "",
        @SerializedName("email", alternate = ["Email"])
        var email: String? = "",
        @SerializedName("telcode", alternate = ["TelephoneCode"])
        var telCode: Int? = null,
        @SerializedName("AccountName")
        var accountName: String? = "",
        @SerializedName("AccountTypeCode")
        var accountTypeCode: String? = "",
        @SerializedName("GeoAddressID")
        var addressId: Int? = 0,
        @SerializedName("CountryCode")
        var countryCode: String? = "",
        @SerializedName("Country")
        var country: String? = "",
        @SerializedName("State")
        var state: String? = "",
        @SerializedName("City")
        var city: String? = "",
        @SerializedName("Zone")
        var zone: String? = "",
        @SerializedName("SectorID")
        var sectorId: Int? = 0,
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
        @SerializedName("Parcel")
        var pacel: String? = "",
        @SerializedName("Latitude")
        var latitude: Double? = 0.0,
        @SerializedName("Longitude")
        var longitude: Double? = 0.0,
        @SerializedName("Description")
        var description: String? = "",
        @SerializedName("MobileWithCode")
        var mobileWithCode: String? = "",
        @SerializedName("SycotaxID")
        var sycoTaxId: String? = "",
        @SerializedName("Status")
        var status: String? = "",
        @SerializedName("DrivingLicenseNo")
        var drivingLicenseNo: String? = "",
        @SerializedName("dob", alternate = ["DateOfBirth"])
        var dob: String? = "",
        @Expose(serialize = false, deserialize = false)
        @Transient
        var isLoading: Boolean = false,
        @Transient
        @Expose(serialize = false, deserialize = false)
        var documents: ArrayList<COMDocumentReference>? = arrayListOf(),
        @SerializedName("PhoneNumbers")
        var phoneNumbers: String? = null,
        @SerializedName("Emails")
        var emails: String? = null,
        @SerializedName("OwnerGeoAddressID")
        var ownerGeoAddressID: Int? = null,
        @SerializedName("OwnerCountryCode")
        var ownerCountryCode: String? = null,
        @SerializedName("OwnerCountry")
        var ownerCountry: String? = null,
        @SerializedName("OwnerState")
        var ownerState: String? = null,
        @SerializedName("OwnerCity")
        var ownerCity: String? = null,
        @SerializedName("OwnerZone")
        var ownerZone: String? = null,
        @SerializedName("OwnerSectorID")
        var ownerSectorID: String? = null,
        @SerializedName("OwnerSector")
        var ownerSector: String? = null,
        @SerializedName("OwnerStreet")
        var ownerStreet: String? = null,
        @SerializedName("OwnerZipCode")
        var ownerZipCode: String? = null,
        @SerializedName("OwnerSection")
        var ownerSection: String? = null,
        @SerializedName("OwnerLot")
        var ownerLot: String? = null,
        @SerializedName("OwnerParcel")
        var ownerParcel: String? = null,
        @SerializedName("OwnerLatitude")
        var ownerLatitude: String? = null,
        @SerializedName("OwnerLongitude")
        var ownerLongitude: String? = null,
        @SerializedName("OwnerDescription")
        var ownerDescription: String? = null,
        @SerializedName("CitizenSycotaxID")
        var citizenSycoTaxID: String? = null,
        @SerializedName("CitizenCardNo")
        var citizenCardNo: String? = null,
        @SerializedName("Owners")
        var owners: String? = "",
        @SerializedName("OwnerNumbers")
        var ownersNumber: String? = "",


) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
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
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readByte() != 0.toByte(),
            parcel.createTypedArrayList(COMDocumentReference),
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
    )


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(statusCode)
        parcel.writeValue(professionID)
        parcel.writeString(remarks)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(contactName)
        parcel.writeString(ifu)
        parcel.writeValue(taxPayerAccountID)
        parcel.writeValue(contactID)
        parcel.writeString(profession)
        parcel.writeValue(accountID)
        parcel.writeString(businessOwnerID)
        parcel.writeString(citizenID)
        parcel.writeValue(ownerAccountID)
        parcel.writeValue(accountContactID)
        parcel.writeString(phone)
        parcel.writeString(email)
        parcel.writeValue(telCode)
        parcel.writeString(accountName)
        parcel.writeString(accountTypeCode)
        parcel.writeValue(addressId)
        parcel.writeString(countryCode)
        parcel.writeString(country)
        parcel.writeString(state)
        parcel.writeString(city)
        parcel.writeString(zone)
        parcel.writeValue(sectorId)
        parcel.writeString(sector)
        parcel.writeString(street)
        parcel.writeString(zipCode)
        parcel.writeString(section)
        parcel.writeString(lot)
        parcel.writeString(pacel)
        parcel.writeValue(latitude)
        parcel.writeValue(longitude)
        parcel.writeString(description)
        parcel.writeString(mobileWithCode)
        parcel.writeString(sycoTaxId)
        parcel.writeString(status)
        parcel.writeString(drivingLicenseNo)
        parcel.writeString(dob)
        parcel.writeByte(if (isLoading) 1 else 0)
        parcel.writeTypedList(documents)
        parcel.writeString(phoneNumbers)
        parcel.writeString(emails)
        parcel.writeInt(ownerGeoAddressID ?: 0)
        parcel.writeString(ownerCountryCode)
        parcel.writeString(ownerCountry)
        parcel.writeString(ownerState)
        parcel.writeString(ownerCity)
        parcel.writeString(ownerZone)
        parcel.writeString(ownerSectorID)
        parcel.writeString(ownerSector)
        parcel.writeString(ownerStreet)
        parcel.writeString(ownerZipCode)
        parcel.writeString(ownerSection)
        parcel.writeString(ownerLot)
        parcel.writeString(ownerParcel)
        parcel.writeString(ownerLatitude)
        parcel.writeString(ownerLongitude)
        parcel.writeString(ownerDescription)
        parcel.writeString(citizenSycoTaxID)
        parcel.writeString(citizenCardNo)
        parcel.writeString(owners)
        parcel.writeString(ownersNumber)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BusinessOwnership> {
        override fun createFromParcel(parcel: Parcel): BusinessOwnership {
            return BusinessOwnership(parcel)
        }

        override fun newArray(size: Int): Array<BusinessOwnership?> {
            return arrayOfNulls(size)
        }
    }
}