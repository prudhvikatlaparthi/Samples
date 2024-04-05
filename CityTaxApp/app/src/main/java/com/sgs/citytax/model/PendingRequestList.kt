package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class PendingRequestList(
        @SerializedName("PropertyVerificationRequestID")
        var propertyVerificationRequestId: Int? = 0,
        @SerializedName("PropertyVerificationRequestDate")
        var propertyVerificationReqDate: String? = "",
        @SerializedName("PlanningPermissionRequestID")
        var planninPermissionRequestId: Int? = 0,
        @SerializedName("PropertyVerificationTypeID")
        var propertyVerificationTypeId: Int? = 0,
        @SerializedName("AllowDocumentVerification")
        var allowDocumentVerification: String? = "",
        @SerializedName("AllowPhysicalVerification")
        var allowPhysicalVerification: String? = "",
        @SerializedName("PropertyID")
        var propertyId: Int? = 0,
        @SerializedName("OwnerAccountID")
        var ownerAccountID: Int? = 0,
        @SerializedName("StatusCode")
        var statusCode: String? = "",
        @SerializedName("Description")
        var description: String? = "",
        @SerializedName("DocumentVerificationByUserID")
        var documentVerificationByUSerID: String? = "",
        @SerializedName("DocumentVerificationStatusCode")
        var documentVerificationStatusCode: String? = "",
        @SerializedName("DocumentVerificationRemarks")
        var docVerificationRemarks: String? = "",
        @SerializedName("DocumentVerificatioDate")
        var documentVerificationDate: String? = "",
        @SerializedName("PhysicalVerificationByUserID")
        var physicalVerificationByUSerID: Int? = 0,
        @SerializedName("PhysicalVerificationStatusCode")
        var physicalVerificationStatusCode: String? = "",
        @SerializedName("PhysicalVerificationRemarks")
        var physicalVerificationRemarks: String? = "",
        @SerializedName("PhysicalVerificatioDate")
        var physicalVerificatIonDate: String? = "",
        @SerializedName("Status")
        var status: String? = "",
        @SerializedName("PropertyName")
        var propertyName: String? = "",
        @SerializedName("PropertyVerificationType")
        var propertyVerificationType: String? = "",
        @SerializedName("Owner")
        var ownerName: String? = "",
        @SerializedName("DocumentVerificationByUser")
        var documentVerificationByUser: String? = "",
        @SerializedName("PhysicalVerificationByUser")
        var physicalVerificationByUser: String? = "",
        @SerializedName("DocumentVerificationStatus")
        var documentVerificationStatus: String? = "",
        @SerializedName("PhysicalVerificationStatus")
        var physicalVerificationStatus: String? = "",
        @SerializedName("PropertyOwnershipID")
        var ownershipID: Int? = 0,
        @SerializedName("PropertySycotaxID")
        var sycoTaxId: String? = "",
        @Transient
        var isLoading: Boolean = false,
        @Transient
        var documents: ArrayList<COMDocumentReference>? = arrayListOf(),
        @Transient
        var isDocumentVerified: Boolean = true,
        @Transient
        var isPhysicalVerified: Boolean = true,
        @SerializedName("TaxRuleBookCode")
        var taxRuleBookCode: String? = "",
        @SerializedName("PropertyType")
        var propertyType: String? = ""

) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
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
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readByte() != 0.toByte(),
            parcel.createTypedArrayList(COMDocumentReference),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(propertyVerificationRequestId)
        parcel.writeString(propertyVerificationReqDate)
        parcel.writeValue(planninPermissionRequestId)
        parcel.writeValue(propertyVerificationTypeId)
        parcel.writeString(allowDocumentVerification)
        parcel.writeString(allowPhysicalVerification)
        parcel.writeValue(propertyId)
        parcel.writeValue(ownerAccountID)
        parcel.writeString(statusCode)
        parcel.writeString(description)
        parcel.writeString(documentVerificationByUSerID)
        parcel.writeString(documentVerificationStatusCode)
        parcel.writeString(docVerificationRemarks)
        parcel.writeString(documentVerificationDate)
        parcel.writeValue(physicalVerificationByUSerID)
        parcel.writeString(physicalVerificationStatusCode)
        parcel.writeString(physicalVerificationRemarks)
        parcel.writeString(physicalVerificatIonDate)
        parcel.writeString(status)
        parcel.writeString(propertyName)
        parcel.writeString(propertyVerificationType)
        parcel.writeString(ownerName)
        parcel.writeString(documentVerificationByUser)
        parcel.writeString(physicalVerificationByUser)
        parcel.writeString(documentVerificationStatus)
        parcel.writeString(physicalVerificationStatus)
        parcel.writeValue(ownershipID)
        parcel.writeString(sycoTaxId)
        parcel.writeByte(if (isLoading) 1 else 0)
        parcel.writeTypedList(documents)
        parcel.writeByte(if (isDocumentVerified) 1 else 0)
        parcel.writeByte(if (isPhysicalVerified) 1 else 0)
        parcel.writeString(taxRuleBookCode)
        parcel.writeString(propertyType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PendingRequestList> {
        override fun createFromParcel(parcel: Parcel): PendingRequestList {
            return PendingRequestList(parcel)
        }

        override fun newArray(size: Int): Array<PendingRequestList?> {
            return arrayOfNulls(size)
        }
    }
}