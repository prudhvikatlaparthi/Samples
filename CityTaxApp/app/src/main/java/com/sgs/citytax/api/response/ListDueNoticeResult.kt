package com.sgs.citytax.api.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class ListDueNoticeResult(

    @SerializedName("DueNoticeID")
    var dueNoticeId: Int? = 0,
    @SerializedName("DueNoticeDate")
    var dueNoticeDate: String? = "",
    @SerializedName("RequestNo")
    var requestNo: Int? = 0,
    @SerializedName("AccountID")
    var accountID: Int? = 0,
    @SerializedName("StatusCode")
    var statusCode: String? = "",
    @SerializedName("Status")
    var status: String? = "",
    @SerializedName("DueNoticeType")
    var dueNoticeType: String? = "",
    @SerializedName("LegalAgreementNo")
    var legalAgreementNo: String? = "",
    @SerializedName("Year")
    var year: Int? = 0,
    @SerializedName("Business")
    var businessName: String? = "",
    @SerializedName("SycotaxID")
    var sycoTaxID: String? = "",
    @SerializedName("ContactNumber")
    var contactNumber: String? = "",
    @SerializedName("Email")
    var email: String? = "",
    @SerializedName("Zone")
    var zone: String? = "",
    @SerializedName("NoticeReferenceNo")
    var noticeReferenceNo: String? = "",
    @SerializedName("AgreementReferenceNo")
    var voucherNo: String? = "",
    @SerializedName("AWSPath")
    var awsPath: String? = "",
    @SerializedName("SignatureID")
    var signatureID: String? = "",
    @SerializedName("SignatureAWSPath")
    var signatureAWSPath: String? = "",
    @SerializedName("ReportingDateTime")
    var reportingDateTime: String? = "",
    @SerializedName("RecipientName")
    var recipientName: String? = "",
    @SerializedName("RecipientMobile")
    var mobileNum: String? = ""

):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
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
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(dueNoticeId)
        parcel.writeString(dueNoticeDate)
        parcel.writeValue(requestNo)
        parcel.writeValue(accountID)
        parcel.writeString(statusCode)
        parcel.writeString(status)
        parcel.writeString(dueNoticeType)
        parcel.writeString(legalAgreementNo)
        parcel.writeValue(year)
        parcel.writeString(businessName)
        parcel.writeString(sycoTaxID)
        parcel.writeString(contactNumber)
        parcel.writeString(email)
        parcel.writeString(zone)
        parcel.writeString(noticeReferenceNo)
        parcel.writeString(voucherNo)
        parcel.writeString(awsPath)
        parcel.writeString(signatureID)
        parcel.writeString(signatureAWSPath)
        parcel.writeString(reportingDateTime)
        parcel.writeString(recipientName)
        parcel.writeString(mobileNum)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ListDueNoticeResult> {
        override fun createFromParcel(parcel: Parcel): ListDueNoticeResult {
            return ListDueNoticeResult(parcel)
        }

        override fun newArray(size: Int): Array<ListDueNoticeResult?> {
            return arrayOfNulls(size)
        }
    }
}