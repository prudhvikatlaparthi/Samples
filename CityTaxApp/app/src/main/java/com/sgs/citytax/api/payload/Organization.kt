package com.sgs.citytax.api.payload

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Organization(
        @SerializedName("Organization")
        var organization: String? = "",
        @SerializedName("segid")
        var segmentId: Int? = 0,
        @SerializedName("prntorgid", alternate = ["OwnerOrgBranchID"])
        var parentOrganizationID: Int? = 0,
        @SerializedName("Phone")
        var phone: String? = "",
        @SerializedName("email")
        var email: String? = "",
        @SerializedName("sts")
        var status: String? = "",
        @SerializedName("StatusCode")
        var statusCode: String? = null,
        @SerializedName("ActivityDomainID")
        var activityDomainID: Int? = 0,
        @SerializedName("WebSite")
        var webSite: String? = "",
        @SerializedName("IFU")
        var ifu: String? = "",
        @SerializedName("rmks")
        var remarks: String? = "",
        @SerializedName("acctid")
        var accountID: Int = 0,
        @SerializedName("orgzid")
        var organizationID: Int = 0,
        @SerializedName("CreatedByAccountID")
        var createdByAccountId: Int = 0,
        @SerializedName("SycotaxID")
        var sycotaxID: String? = "",
        @Expose(serialize = false, deserialize = false)
        var activityDomainName: String? = "",
        @SerializedName("long")
        var longitude: Double? = 0.0,
        @SerializedName("lat")
        var latitude: Double? = 0.0,
        @SerializedName("GeoAddressID")
        var geoAddressID: Int? = 0,
        @SerializedName("ActivityClassID")
        var activityClassID: Int? = 0,
        @SerializedName("telcode")
        var telCode: String? = "",
        @Expose(serialize = false, deserialize = false)
        var activityClassName: String? = "",
        @SerializedName("TRNNo")
        var tradeNo: String? = "",
        @SerializedName("EmailVerified")
        var emailVerified: String? = "N",
        @SerializedName("PhoneVerified")
        var phoneVerified: String? = "N",
        @SerializedName("HotelDesFinanceID")
        var hotelDesFinanceID: Int? = 0

) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(organization)
        parcel.writeValue(segmentId)
        parcel.writeValue(parentOrganizationID)
        parcel.writeString(phone)
        parcel.writeString(email)
        parcel.writeString(status)
        parcel.writeString(statusCode)
        parcel.writeValue(activityDomainID)
        parcel.writeString(webSite)
        parcel.writeString(ifu)
        parcel.writeString(remarks)
        parcel.writeInt(accountID)
        parcel.writeInt(organizationID)
        parcel.writeInt(createdByAccountId)
        parcel.writeString(sycotaxID)
        parcel.writeString(activityDomainName)
        parcel.writeValue(longitude)
        parcel.writeValue(latitude)
        parcel.writeValue(geoAddressID)
        parcel.writeValue(activityClassID)
        parcel.writeString(telCode)
        parcel.writeString(activityClassName)
        parcel.writeString(tradeNo)
        parcel.writeString(emailVerified)
        parcel.writeString(phoneVerified)
        parcel.writeValue(hotelDesFinanceID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Organization> {
        override fun createFromParcel(parcel: Parcel): Organization {
            return Organization(parcel)
        }

        override fun newArray(size: Int): Array<Organization?> {
            return arrayOfNulls(size)
        }
    }
}