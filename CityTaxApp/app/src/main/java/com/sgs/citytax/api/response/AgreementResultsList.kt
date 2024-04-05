package com.sgs.citytax.api.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.PropertyTax4Business

data class AgreementResultsList(
        @SerializedName("DueAgreementID")
        var dueAgreementID : Int? = null,
        @SerializedName("DueAgreementDate")
        val dueAgreementDate: String? = "",
        @SerializedName("DueNoticeID")
        val dueNoticeID: Int? = 0,
        @SerializedName("DocumentID")
        val documentID: Int? = 0,
        @SerializedName("ValidUptoDate")
        val validUptoDate: String? = "",
        @SerializedName("ReferenceNo")
        var referenceNo: String? = "",
        @SerializedName("LegalAgreementNo")
        val legalAgreementNo: String? = "",
        @SerializedName("Remarks")
        val remarks: String? = "",
        @SerializedName("StatusCode")
        val statusCode: String? = "",
        @SerializedName("Status")
        val status: String? = "",
        @SerializedName("Business")
        val businessName: String? = "",
        @SerializedName("SycotaxID")
        val businessSyco: String? = "",
        @SerializedName("ContactNumber")
        val contactNumber: String? = "",
        @SerializedName("Email")
        val email: String? = "",
        @SerializedName("AccountID")
        val accountID: Int? = 0,
        @SerializedName("AWSPath")
        val awsPath: String? = "",
        @SerializedName("NoticeReferenceNo")
        val noticeReferenceNo: String? = "",
        @SerializedName("AllowEdit")
        val allowEdit: String? = "",
        @SerializedName("DueNoticeType")
        val dueNoticeType: String? = ""
): Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readString(),
                parcel.readValue(Int::class.java.classLoader) as? Int,
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
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString()
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeValue(dueAgreementID)
                parcel.writeString(dueAgreementDate)
                parcel.writeValue(dueNoticeID)
                parcel.writeValue(documentID)
                parcel.writeString(validUptoDate)
                parcel.writeString(referenceNo)
                parcel.writeString(legalAgreementNo)
                parcel.writeString(remarks)
                parcel.writeString(statusCode)
                parcel.writeString(status)
                parcel.writeString(businessName)
                parcel.writeString(businessSyco)
                parcel.writeString(contactNumber)
                parcel.writeString(email)
                parcel.writeValue(accountID)
                parcel.writeString(awsPath)
                parcel.writeString(noticeReferenceNo)
                parcel.writeString(allowEdit)
                parcel.writeString(dueNoticeType)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<AgreementResultsList> {
                override fun createFromParcel(parcel: Parcel): AgreementResultsList {
                        return AgreementResultsList(parcel)
                }

                override fun newArray(size: Int): Array<AgreementResultsList?> {
                        return arrayOfNulls(size)
                }
        }
}


/*

   "DueAgreementID": 3,
                "DueAgreementDate": "2021-08-31T14:44:35.600",
                "DueNoticeID": 222,
                "DocumentID": 28055,
                "ValidUptoDate": "2021-08-27T00:00:00.000",
                "ReferenceNo": null,
                "Remarks": "test marks1234",
                "StatusCode": "ACC_DueAgreements.New",
                "Status": "Ouvert",
                "Business": "SMR Stores",
                "SycotaxID": "SYC-OUA-1001-001088",
                "Year": 2021,
                "ContactNumber": "308741222",
                "Email": "smr.987@e.com",
                "AccountID": 3369,
                "AWSPath": "https://citytaxobjectstore.justbilling.co/Store//uat\\citytax//28055.png"

*/
