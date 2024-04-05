package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class VUCRMAccounts(
        @SerializedName("acctname", alternate = ["BusinessName","Owners"])
        var accountName: String? = "",
        @SerializedName("accttyp")
        var accountType: String? = "",
        @SerializedName("accttypcode")
        var accountTypeCode: String? = "",
        @SerializedName("IFU")
        var ifu: String? = "",
        @SerializedName("sts")
        var status: String? = "",
        @SerializedName("stscode")
        var statusCode: String? = "",
        @SerializedName("acctid")
        var accountId: Int? = 0,
        @SerializedName("orgzid")
        var organizationId: Int? = 0,
        @SerializedName("site", alternate = ["WebSite"])
        var website: String? = "",
        @SerializedName("Number", alternate = ["mob"])
        var phone: String? = null,
        @SerializedName("Email", alternate = ["email"])
        var email: String? = null,
        @SerializedName("rmks")
        var remarks: String? = "",
        @SerializedName("ActivityDomain")
        var activityDomain: String? = "",
        @SerializedName("ActivityDomainID")
        var activityDomainID: Int? = 0,
        @Expose(serialize = false, deserialize = false)
        var isLoading: Boolean = false,
        @SerializedName("TaskCodeList")
        var taskCodeList: List<TaskCode>? = arrayListOf(),
        @SerializedName("SycotaxID")
        var sycoTaxID: String? = "",
        @SerializedName("segid")
        var segmentId: Int? = 0,
        @SerializedName("seg")
        var segment: String? = "",
        @SerializedName("OwnerOrgBranchID", alternate = ["prntorgid"])
        var parentOrganizationID: Int? = 0,
        @SerializedName("EstimatedTax")
        var estimatedTax: BigDecimal = BigDecimal.ZERO,
        @SerializedName("lat")
        var latitude: String? = "",
        @SerializedName("long")
        var longitude: String? = "",
        @SerializedName("GeoAddressID")
        var geoAddressID: Int? = 0,
        @SerializedName("ActivityClassID")
        var activityClassID: Int? = 0,
        @SerializedName("ActivityClassName")
        var activityClassName: String? = "",
        @SerializedName("TRNNo")
        var tradeNo: String? = "",
        @SerializedName("telcode")
        var telephoneCode: Int? = null,
        @SerializedName("EmailVerified")
        var emailVerified: String? = "N",
        @SerializedName("PhoneVerified")
        var phoneVerified: String? = "N",
        @SerializedName("HotelDesFinanceID")
        var hotelDesFinanceID: Int? = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readByte() != 0.toByte(),
            parcel.createTypedArrayList(TaskCode),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readSerializable() as BigDecimal,
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
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(accountName)
        parcel.writeString(accountType)
        parcel.writeString(accountTypeCode)
        parcel.writeString(ifu)
        parcel.writeString(status)
        parcel.writeString(statusCode)
        parcel.writeValue(accountId)
        parcel.writeValue(organizationId)
        parcel.writeString(website)
        parcel.writeString(phone)
        parcel.writeString(email)
        parcel.writeString(remarks)
        parcel.writeString(activityDomain)
        parcel.writeValue(activityDomainID)
        parcel.writeByte(if (isLoading) 1 else 0)
        parcel.writeTypedList(taskCodeList)
        parcel.writeString(sycoTaxID)
        parcel.writeValue(segmentId)
        parcel.writeString(segment)
        parcel.writeValue(parentOrganizationID)
        parcel.writeSerializable(estimatedTax)
        parcel.writeString(latitude)
        parcel.writeString(longitude)
        parcel.writeValue(geoAddressID)
        parcel.writeValue(activityClassID)
        parcel.writeString(activityClassName)
        parcel.writeString(tradeNo)
        parcel.writeValue(telephoneCode)
        parcel.writeString(emailVerified)
        parcel.writeString(phoneVerified)
        parcel.writeValue(hotelDesFinanceID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VUCRMAccounts> {
        override fun createFromParcel(parcel: Parcel): VUCRMAccounts {
            return VUCRMAccounts(parcel)
        }

        override fun newArray(size: Int): Array<VUCRMAccounts?> {
            return arrayOfNulls(size)
        }
    }
}