package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CRMAgents(
        var AgentID: Int? = 0,
        var AgentTypeID: Int? = 0,
        var AgentType: String? = "",
        @SerializedName("Salutation", alternate = ["saluttn"])
        var Salutation: String? = "",
        @SerializedName("frstname", alternate = ["FirstName"])
        var FirstName: String? = "",
        @SerializedName("mddlename", alternate = ["MiddleName"])
        var MiddleName: String? = "",
        @SerializedName("lastname", alternate = ["LastName"])
        var LastName: String? = "",
        var ParentAgentID: Int? = 0,
        @SerializedName("ParentAgentName")
        var ParentAgentName: String? = "",
        var AgentUserID: String? = "",
        @SerializedName("ownrorgbrid", alternate = ["OwnerOrgBranchID"])
        var OwnerOrgBranchID: Int? = 0,
        @SerializedName("email", alternate = ["Email"])
        var email: String? = "",
        @SerializedName("mob", alternate = ["Mobile"])
        var mobileNo: String? = "",
        @SerializedName("stscode", alternate = ["StatusCode"])
        var StatusCode: String? = null,
        @SerializedName("sts", alternate = ["Status"])
        var Status: String? = null,
        @SerializedName("AgentName")
        var agentName: String? = null,
        @SerializedName("AgentCode")
        var agentCode: String? = null,
        @Expose(serialize = false, deserialize = false)
        var isLoading: Boolean = false,
        @SerializedName("rmks")
        var remarks: String? = null,
        @SerializedName("VerifiedByUserID")
        var verifiedByUserID: String? = "",
        @SerializedName("CreatedByAccountID")
        var createdByAccountID: Int = 0,
        @SerializedName("telcode", alternate = ["TelephoneCode"])
        var telephoneCode: Int? = null,
        @SerializedName("AssignedZoneCode")
        var assignedZoneCode: String? = null,
        @SerializedName("HotelDesFinanceID")
        var hotelDesFinanceID: Int? = 0,

) : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
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
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int
    )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(AgentID)
        parcel.writeValue(AgentTypeID)
        parcel.writeString(AgentType)
        parcel.writeString(Salutation)
        parcel.writeString(FirstName)
        parcel.writeString(MiddleName)
        parcel.writeString(LastName)
        parcel.writeValue(ParentAgentID)
        parcel.writeString(ParentAgentName)
        parcel.writeString(AgentUserID)
        parcel.writeValue(OwnerOrgBranchID)
        parcel.writeString(email)
        parcel.writeString(mobileNo)
        parcel.writeString(StatusCode)
        parcel.writeString(Status)
        parcel.writeString(agentName)
        parcel.writeString(agentCode)
        parcel.writeByte(if (isLoading) 1 else 0)
        parcel.writeString(remarks)
        parcel.writeString(verifiedByUserID)
        parcel.writeInt(createdByAccountID)
        parcel.writeValue(telephoneCode)
        parcel.writeString(assignedZoneCode)
        parcel.writeValue(hotelDesFinanceID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CRMAgents> {
        override fun createFromParcel(parcel: Parcel): CRMAgents {
            return CRMAgents(parcel)
        }

        override fun newArray(size: Int): Array<CRMAgents?> {
            return arrayOfNulls(size)
        }
    }

}