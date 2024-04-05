package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class LicenseDetails(
        @SerializedName("LicenseID")
        var licenseId: Int? = 0,
        @com.google.gson.annotations.SerializedName("orgzid")
        var organizationId: Int? = 0,
        @SerializedName("LicenseRequestID")
        var licenseRequestId: Int? = 0,
        @SerializedName("licnsno")
        var licenseNo: String? = "",
        @SerializedName("LicenseCategoryID")
        var licenseCategoryId: Int? = 0,
        @SerializedName("IssuanceDate")
        var issueanceDate: String? = "",
        @SerializedName("stscode")
        var statusCode: String? = "",
        @SerializedName("CancelledByUserID")
        var canceledByUserId: String? = "",
        @SerializedName("CancellationDate")
        var cancellationDate: String? = "",
        @SerializedName("CancellationRemarks")
        var cancellationRemarks: String? = "",
        @SerializedName("AuthorizedBeverages")
        var authorisedBevarages: String? = "",
        @SerializedName("LicenseCategory")
        var licenseCategory: String? = "",
        @SerializedName("sts")
        var status: String? = "",
        @SerializedName("acctid")
        var accountId: Int? = 0,
        @SerializedName("acctname")
        var accountName: String? = "",
        @SerializedName("SycoTaxID")
        var sycoTaxId: String? = "",
        @SerializedName("BillingCycle")
        var billingCycle: String? = "",
        @SerializedName("RateCycle")
        var rateCycle: String? = "",
        @SerializedName("ValidFromDate")
        var validFromDate: String? = "",
        @SerializedName("ValidToDate")
        var validupToDate: String? = "",
        @SerializedName("RenewPending")
        var renewPending: String? = "",
        @SerializedName("CurrentDue")
        var currentDue: Double? = 0.0,
        @SerializedName("EstimatedTaxAmount")
        var estimatedTaxAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("ValidTillDate")
        var validTillDate: String? = "",
        @SerializedName("AllowDelete")
        var allowDelete: String? = "",
        @Transient
        var isLoading: Boolean = false,
        var documents: ArrayList<COMDocumentReference> = arrayListOf()
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
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
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(BigDecimal::class.java.classLoader) as BigDecimal,
            parcel.readString(),
            parcel.readString(),
            parcel.readByte() != 0.toByte()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(licenseId)
        parcel.writeValue(organizationId)
        parcel.writeValue(licenseRequestId)
        parcel.writeString(licenseNo)
        parcel.writeValue(licenseCategoryId)
        parcel.writeString(issueanceDate)
        parcel.writeString(statusCode)
        parcel.writeString(canceledByUserId)
        parcel.writeString(cancellationDate)
        parcel.writeString(cancellationRemarks)
        parcel.writeString(authorisedBevarages)
        parcel.writeString(licenseCategory)
        parcel.writeString(status)
        parcel.writeValue(accountId)
        parcel.writeString(accountName)
        parcel.writeString(sycoTaxId)
        parcel.writeString(billingCycle)
        parcel.writeString(rateCycle)
        parcel.writeString(validFromDate)
        parcel.writeString(validupToDate)
        parcel.writeString(renewPending)
        parcel.writeValue(currentDue)
        parcel.writeValue(estimatedTaxAmount)
        parcel.writeString(validTillDate)
        parcel.writeString(allowDelete)
        parcel.writeByte(if (isLoading) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LicenseDetails> {
        override fun createFromParcel(parcel: Parcel): LicenseDetails {
            return LicenseDetails(parcel)
        }

        override fun newArray(size: Int): Array<LicenseDetails?> {
            return arrayOfNulls(size)
        }
    }
}