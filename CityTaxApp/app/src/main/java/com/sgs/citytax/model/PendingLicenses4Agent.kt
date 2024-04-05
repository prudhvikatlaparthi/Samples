package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class PendingLicenses4Agent(
        @SerializedName("LicenseID")
        var licenseID: Int? = 0,
        @SerializedName("orgzid")
        var orgzid: Int? = 0,
        @SerializedName("LicenseRequestID")
        var licenseRequestID: Int? = 0,
        @SerializedName("licnsno", alternate = ["LicenseNo"])
        var licenseNumber: String? = "",
        @SerializedName("LicenseCategoryID")
        var licenseCategoryID: Int? = 0,
        @SerializedName("IssuanceDate")
        var issuanceDate: String? = "",
        @SerializedName("stscode")
        var statusCode: String? = "",
        @SerializedName("CancelledByUserID")
        var cancelledByUserID: Int? = 0,
        @SerializedName("CancellationDate")
        var cancellationDate: String? = "",
        @SerializedName("CancellationRemarks")
        var cancellationRemarks: String? = "",
        @SerializedName("AuthorizedBeverages")
        var authorizedBeverages: String? = "",
        @SerializedName("LicenseCategory")
        var licenseCategory: String? = "",
        @SerializedName("sts")
        var status: String? = "",
        @SerializedName("acctid" , alternate = ["AccountID"])
        var accountId: Int? = 0,
        @SerializedName("acctname" , alternate = ["AccountName"])
        var accountName: String? = "",
        @SerializedName("SycotaxID")
        var sycoTaxID: String? = "",
        @SerializedName("BillingCyle")
        var billingCyle: String? = "",
        @SerializedName("RateCycle")
        var rateCycle: String? = "",
        @SerializedName("ValidFromDate")
        var validFromDate: String? = "",
        @SerializedName("ValidToDate")
        var validToDate: String? = "",
        @SerializedName("RenewPending")
        var renewPending: String? = "",
        @SerializedName("CurrentDue")
        var currentDue: BigDecimal? = BigDecimal.ZERO
) : Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int,
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
                parcel.readString(),
                parcel.readValue(BigDecimal::class.java.classLoader) as? BigDecimal) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeValue(licenseID)
                parcel.writeValue(orgzid)
                parcel.writeValue(licenseRequestID)
                parcel.writeString(licenseNumber)
                parcel.writeValue(licenseCategoryID)
                parcel.writeString(issuanceDate)
                parcel.writeString(statusCode)
                parcel.writeValue(cancelledByUserID)
                parcel.writeString(cancellationDate)
                parcel.writeString(cancellationRemarks)
                parcel.writeString(authorizedBeverages)
                parcel.writeString(licenseCategory)
                parcel.writeString(status)
                parcel.writeValue(accountId)
                parcel.writeString(accountName)
                parcel.writeString(sycoTaxID)
                parcel.writeString(billingCyle)
                parcel.writeString(rateCycle)
                parcel.writeString(validFromDate)
                parcel.writeString(validToDate)
                parcel.writeString(renewPending)
                parcel.writeValue(currentDue)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<PendingLicenses4Agent> {
                override fun createFromParcel(parcel: Parcel): PendingLicenses4Agent {
                        return PendingLicenses4Agent(parcel)
                }

                override fun newArray(size: Int): Array<PendingLicenses4Agent?> {
                        return arrayOfNulls(size)
                }
        }

        override fun toString(): String {
                return accountName + "\n" + licenseNumber + "\n"+ sycoTaxID
        }
}