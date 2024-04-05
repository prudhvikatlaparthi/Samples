package com.sgs.citytax.api.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class VUCRMServiceRequest(
    @SerializedName("Street")
    var street: String? = null,
    @SerializedName("zip")
    var zip: String? = null,
    @SerializedName("Plot")
    var plot: String? = null,
    @SerializedName("Block")
    var block: String? = null,
    @SerializedName("doorno")
    var doorNo: String? = null,
    @SerializedName("area")
    var area: String? = null,
        @SerializedName("AssignTime")
        val assignTime: String? = "",
        @SerializedName("AssignedTo")
        val assignedTo: String? = "",
        @SerializedName("AssignedToUserID",alternate = ["assgnd2usrid"])
        val assignedToUserID: String? = "",
        @SerializedName("ClosedTime")
        val closedTime: String? = "",
        @SerializedName("Complaint")
        val complaint: String? = "",
        @SerializedName("ComplaintDate")
        val complaintDate: String? = "",
        @SerializedName("CreatedBy")
        val createdBy: String? = "",
        @SerializedName("IssueDescription",alternate = ["issdesc"])
        var issueDescription: String? = "",
        @SerializedName("Latitude",alternate = ["lat"])
        var latitude: Double? = 0.0,
        @SerializedName("Longitude",alternate = ["long"])
        var longitude: Double? = 0.0,
        @SerializedName("ServiceRequestNo",alternate = ["svcreqno"])
        var serviceRequestNo: Int? = 0,
        @SerializedName("StartTime",alternate = ["strttime"])
        val startTime: String? = "",
        @SerializedName("StatusCode", alternate = ["stscode"])
        val statusCode: String? = "",
        @SerializedName("Status", alternate = ["sts"])
        var status: String? = "",
        @SerializedName("tgtendtime")
        val targetEndTime: String? = "",
        @SerializedName("TaskType")
        val taskType: String? = "",
        @SerializedName("TaskCategory")
        val taskCategory: String? = "",
        @SerializedName("ServiceRequestDate",alternate = ["svcreqdt"])
        var serviceRequestDate: String? = "",
        @SerializedName("ParentServiceRequestNo")
        val parentServiceRequestNo: String? = "",
        @SerializedName("Incident")
        var incident: String? = "",
        @SerializedName("IncidentID")
        var incidentID: Int? = 0,
        @SerializedName("CreatedDate",alternate = ["crtddt"])
        val createdDate: String? = "",
        @SerializedName("AccountID",alternate = ["acctid"])
        val accountID: Int? = 0,
        @SerializedName("Priority",alternate = ["pri"])
        var priority: String? = "",
        @SerializedName("IncidentSubtypeID")
        var incidentSubtypeID: Int? = 0,
        @SerializedName("IncidentSubtype")
        var incidentSubtype: String? = null,
        @SerializedName("AccountName")
        var accountName: String? = null,
        @SerializedName("TaskSubCategory")
        val taskSubCategory: String? = null,
        @SerializedName("TaskSubCategoryID")
        val taskSubCategoryID: Int? = 0,
        @Transient
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
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
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
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(street)
        parcel.writeString(zip)
        parcel.writeString(plot)
        parcel.writeString(block)
        parcel.writeString(doorNo)
        parcel.writeString(area)
        parcel.writeString(assignTime)
        parcel.writeString(assignedTo)
        parcel.writeString(assignedToUserID)
        parcel.writeString(closedTime)
        parcel.writeString(complaint)
        parcel.writeString(complaintDate)
        parcel.writeString(createdBy)
        parcel.writeString(issueDescription)
        parcel.writeValue(latitude)
        parcel.writeValue(longitude)
        parcel.writeValue(serviceRequestNo)
        parcel.writeString(startTime)
        parcel.writeString(statusCode)
        parcel.writeString(status)
        parcel.writeString(targetEndTime)
        parcel.writeString(taskType)
        parcel.writeString(taskCategory)
        parcel.writeString(serviceRequestDate)
        parcel.writeString(parentServiceRequestNo)
        parcel.writeString(incident)
        parcel.writeValue(incidentID)
        parcel.writeString(createdDate)
        parcel.writeValue(accountID)
        parcel.writeString(priority)
        parcel.writeValue(incidentSubtypeID)
        parcel.writeString(incidentSubtype)
        parcel.writeString(accountName)
        parcel.writeString(taskSubCategory)
        parcel.writeValue(taskSubCategoryID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VUCRMServiceRequest> {
        override fun createFromParcel(parcel: Parcel): VUCRMServiceRequest {
            return VUCRMServiceRequest(parcel)
        }

        override fun newArray(size: Int): Array<VUCRMServiceRequest?> {
            return arrayOfNulls(size)
        }
    }
}