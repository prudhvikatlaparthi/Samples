package com.sgs.citytax.api.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class IncidentDetailLocation(
        @SerializedName("IncidentType")
        var incidentType: String? = null,
        @SerializedName("IncidentNo")
        var incidentNo: Int? = 0,
        @SerializedName("IncidentDate")
        var incidentDate: String? = null,
        @SerializedName("Title")
        var title: String? = null,
        @SerializedName("IssueDescription")
        var issueDescription: String? = null,
        @SerializedName("Status")
        var status: String? = null,
        @SerializedName("ReportedBy")
        var reportedBy: String? = null,
        @SerializedName("Zone")
        var zone: String? = null,
        @SerializedName("Sector")
        var sector: String? = null,
        @SerializedName("Latitude")
        var latitude: String? = null,
        @SerializedName("Longitude")
        var longitude: String? = null,
        @SerializedName("Color")
        var color: String? = null,
        @SerializedName("Legend")
        var legend: String? = null,
        @SerializedName("IncidentSubtype")
        var incidentSubtype: String? = null,
        @SerializedName("IncidentSubtypeID")
        var incidentSubtypeID: Int? = 0,
        @SerializedName("Priority")
        var priority: String? = null,
        @SerializedName("IncidentID")
        val incidentID: Int? = 0,
        var fromDate: String? = null,
        var toDate: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
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
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(incidentType)
        parcel.writeValue(incidentNo)
        parcel.writeString(incidentDate)
        parcel.writeString(title)
        parcel.writeString(issueDescription)
        parcel.writeString(status)
        parcel.writeString(reportedBy)
        parcel.writeString(zone)
        parcel.writeString(sector)
        parcel.writeString(latitude)
        parcel.writeString(longitude)
        parcel.writeString(color)
        parcel.writeString(legend)
        parcel.writeString(incidentSubtype)
        parcel.writeValue(incidentSubtypeID)
        parcel.writeString(priority)
        parcel.writeValue(incidentID)
        parcel.writeString(fromDate)
        parcel.writeString(toDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<IncidentDetailLocation> {
        override fun createFromParcel(parcel: Parcel): IncidentDetailLocation {
            return IncidentDetailLocation(parcel)
        }

        override fun newArray(size: Int): Array<IncidentDetailLocation?> {
            return arrayOfNulls(size)
        }
    }
}