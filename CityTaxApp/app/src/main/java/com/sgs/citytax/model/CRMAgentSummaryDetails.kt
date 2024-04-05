package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class CRMAgentSummaryDetails(
        @SerializedName("IsApprover") var isApprover: String? = "",
        @SerializedName("Prepaid") var isPrepaid: String? = "",
        @SerializedName("brname") var brname: String? = "",
        @SerializedName("acctid") var acctid: String? = "",
        @SerializedName("AgentType") var agentType: String? = "",
        @SerializedName("email") var email: String? = "",
        @SerializedName("mob") var mob: String? = "",
        @SerializedName("saluttn") var saluttn: String? = "",
        @SerializedName("MerchantID") var merchantID: String? = "",
        @SerializedName("APIUserID") var aPIUserID: String? = "",
        @SerializedName("APIUserPwd") var aPIUserPwd: String? = "",
        @SerializedName("frstname") var frstname: String? = "",
        @SerializedName("mddlename") var mddlename: String? = "",
        @SerializedName("lastname") var lastname: String? = "",
        @SerializedName("AgentName") var agentName: String? = "",
        @SerializedName("AgentUserID") var agentUserID: String? = "",
        @SerializedName("crtd") var crtd: String? = "",
        @SerializedName("crtddt") var crtddt: String? = "",
        @SerializedName("mdfd") var mdfd: String? = "",
        @SerializedName("mdfddt") var mdfddt: String? = "",
        @SerializedName("ParentAgentName") var parentAgentName: String? = "",
        @SerializedName("stscode") var stscode: String? = "",
        @SerializedName("AgentID") var agentID: Int? = 0,
        @SerializedName("AgentTypeID") var agentTypeID: Int? = 0,
        @SerializedName("ParentAgentID") var parentAgentID: Int? = 0,
        @SerializedName("ownrorgbrid") var ownrorgbrid: Int? = 0,
        @SerializedName("CreditBalance") var creditBalance: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("CapAmount") var capAmount: BigDecimal? = BigDecimal.ZERO,
        var isLoading: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
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
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            TODO("creditBalance"),
            TODO("capAmount"),
            parcel.readByte() != 0.toByte()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(isApprover)
        parcel.writeString(isPrepaid)
        parcel.writeString(brname)
        parcel.writeString(acctid)
        parcel.writeString(agentType)
        parcel.writeString(email)
        parcel.writeString(mob)
        parcel.writeString(saluttn)
        parcel.writeString(merchantID)
        parcel.writeString(aPIUserID)
        parcel.writeString(aPIUserPwd)
        parcel.writeString(frstname)
        parcel.writeString(mddlename)
        parcel.writeString(lastname)
        parcel.writeString(agentName)
        parcel.writeString(agentUserID)
        parcel.writeString(crtd)
        parcel.writeString(crtddt)
        parcel.writeString(mdfd)
        parcel.writeString(mdfddt)
        parcel.writeString(parentAgentName)
        parcel.writeString(stscode)
        parcel.writeValue(agentID)
        parcel.writeValue(agentTypeID)
        parcel.writeValue(parentAgentID)
        parcel.writeValue(ownrorgbrid)
        parcel.writeByte(if (isLoading) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CRMAgentSummaryDetails> {
        override fun createFromParcel(parcel: Parcel): CRMAgentSummaryDetails {
            return CRMAgentSummaryDetails(parcel)
        }

        override fun newArray(size: Int): Array<CRMAgentSummaryDetails?> {
            return arrayOfNulls(size)
        }
    }
}