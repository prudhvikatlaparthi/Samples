package com.sgs.citytax.api.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VUCRMServiceTaxRequest(
    @SerializedName("TentativeRequestedDate")
    var tentativeRequestedDate: String? = null,
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
    @SerializedName("AssignedToUserID", alternate = ["assgnd2usrid"])
    val assignedToUserID: String? = "",
    @SerializedName("ClosedTime")
    val closedTime: String? = "",
    @SerializedName("Complaint")
    val complaint: String? = "",
    @SerializedName("ComplaintDate")
    val complaintDate: String? = "",
    @SerializedName("CreatedBy")
    val createdBy: String? = "",
    @SerializedName("IssueDescription", alternate = ["issdesc"])
    var issueDescription: String? = "",
    @SerializedName("Latitude", alternate = ["lat"])
    var latitude: Double? = 0.0,
    @SerializedName("Longitude", alternate = ["long"])
    var longitude: Double? = 0.0,
    @SerializedName("ServiceRequestNo", alternate = ["svcreqno"])
    var serviceRequestNo: Int? = 0,
    @SerializedName("StartTime", alternate = ["strttime"])
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
    @SerializedName("ServiceRequestDate", alternate = ["svcreqdt"])
    var serviceRequestDate: String? = "",
    @SerializedName("ParentServiceRequestNo")
    val parentServiceRequestNo: String? = "",
    @SerializedName("Incident")
    var incident: String? = "",
    @SerializedName("IncidentID")
    var incidentID: Int? = 0,
    @SerializedName("CreatedDate", alternate = ["crtddt"])
    val createdDate: String? = "",
    @SerializedName("AccountID", alternate = ["acctid"])
    val accountID: Int? = 0,
    @SerializedName("Priority", alternate = ["pri"])
    var priority: String? = "",
    @SerializedName("IncidentSubtypeID")
    var incidentSubtypeID: Int? = 0,
    @SerializedName("IncidentSubtype")
    var incidentSubtype: String? = null,
    @SerializedName("TaskSubCategory")
    val taskSubCategory: String? = null,
    @SerializedName("TaskSubCategoryID")
    val taskSubCategoryID: Int? = 0,
    @SerializedName("znid")
    val znid: Int? = 0,
    @SerializedName("SectorID")
    val sectorID: Int? = 0,
    @Transient
    var isLoading: Boolean = false
) : Parcelable