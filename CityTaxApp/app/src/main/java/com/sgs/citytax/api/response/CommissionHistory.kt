package com.sgs.citytax.api.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class CommissionHistory(
        @SerializedName("advdt")
        var advanceDate: String? = "",
        @SerializedName("acctid", alternate = ["AccountID"])
        var accountId: Int? = 0,
        @SerializedName("refno")
        var referenceNo: String? = "",
        @SerializedName("refdt")
        var referanceDate: String? = "",
        @SerializedName("rmks")
        var remarks: String? = "Requesting for Commission Payout",
        @SerializedName("netpaybl")
        var netPayable: Double? = 0.0,
        @SerializedName("advpdid")
        var advancePaidId: Int? = 0,
        @SerializedName("apprvddt")
        var approvedDate: String? = "",
        @SerializedName("stscode")
        var statusCode: String? = "",
        @SerializedName("ApprovedByAccountID")
        var approvedByAccountId: Int? = 0,
        @SerializedName("IsSelfRecharge")
        var isSelfRecharge: String? = "Y",
        @SerializedName("AccountName",alternate = ["acctname"])
        var accountName: String? = "",
        @SerializedName("ApproverName")
        var approverName: String? = "",
        @SerializedName("sts")
        var status: String? = "",
        @SerializedName("AgentCode")
        var agentCode: Int? = 0,
        @SerializedName("pmtmode")
        var paymentMode: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(advanceDate)
        parcel.writeValue(accountId)
        parcel.writeString(referenceNo)
        parcel.writeString(referanceDate)
        parcel.writeString(remarks)
        parcel.writeValue(netPayable)
        parcel.writeValue(advancePaidId)
        parcel.writeString(approvedDate)
        parcel.writeString(statusCode)
        parcel.writeValue(approvedByAccountId)
        parcel.writeString(isSelfRecharge)
        parcel.writeString(accountName)
        parcel.writeString(approverName)
        parcel.writeString(status)
        parcel.writeValue(agentCode)
        parcel.writeString(paymentMode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CommissionHistory> {
        override fun createFromParcel(parcel: Parcel): CommissionHistory {
            return CommissionHistory(parcel)
        }

        override fun newArray(size: Int): Array<CommissionHistory?> {
            return arrayOfNulls(size)
        }
    }
}