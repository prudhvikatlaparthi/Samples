package com.sgs.citytax.api.payload

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Agent(
        @SerializedName("AgentID")
        var agentid: Int? = 0,
        @SerializedName("AgentTypeID")
        var agenttypeid: Int? = 0,
        @SerializedName("saluttn")
        var salutation: String? = null,
        @SerializedName("frstname")
        var frstname: String? = null,
        @SerializedName("mddlename")
        var mddlename: String? = null,
        @SerializedName("lastname")
        var lastname: String? = null,
        @SerializedName("AgentUserID")
        var agentUserID: String? = null,
        @SerializedName("ownrorgbrid")
        var ownrorgbrid: Int? = 0,
        @SerializedName("email")
        var email: String? = null,
        @SerializedName("mob")
        var mobile: String? = null,
        @SerializedName("pwd")
        var password: String? = "",
        @SerializedName("NewPassword")
        var newPassword: String? = ""
) : Parcelable {
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
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(agentid)
        parcel.writeValue(agenttypeid)
        parcel.writeString(salutation)
        parcel.writeString(frstname)
        parcel.writeString(mddlename)
        parcel.writeString(lastname)
        parcel.writeString(agentUserID)
        parcel.writeValue(ownrorgbrid)
        parcel.writeString(email)
        parcel.writeString(mobile)
        parcel.writeString(password)
        parcel.writeString(newPassword)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Agent> {
        override fun createFromParcel(parcel: Parcel): Agent {
            return Agent(parcel)
        }

        override fun newArray(size: Int): Array<Agent?> {
            return arrayOfNulls(size)
        }
    }
}