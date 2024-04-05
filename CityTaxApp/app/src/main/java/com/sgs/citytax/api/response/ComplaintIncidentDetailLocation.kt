package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class ComplaintIncidentDetailLocation(
        @SerializedName("Complaint")
        var complaint: String? = null,
        @SerializedName("ComplaintNo")
        var complaintNo: Int? = 0,
        @SerializedName("ComplaintDate")
        var complaintDate: String? = null,
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
        @SerializedName("ComplaintSubtype")
        var complaintSubtype: String? = null,
        @SerializedName("Priority")
        var priority: String? = null,
        var fromDate: String? = null,
        var toDate: String? = null

) {
    override fun toString(): String {
        return "ComplaintLocations(Complaint=$complaint)"
    }

}