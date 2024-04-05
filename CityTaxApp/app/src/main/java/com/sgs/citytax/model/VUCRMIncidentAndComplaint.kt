package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class VUCRMIncidentAndComplaint(
        @SerializedName("ContactID") var ContactID: String?,
        @SerializedName("ServiceRequestDate") var ServiceRequestDate: String?,
        @SerializedName("ProductCode") var ProductCode: Int?,
        @SerializedName("Title") var Title: String?,
        @SerializedName("Category") var Category: Int?,
        @SerializedName("Priority") var Priority: Int?,
        @SerializedName("Severity") var Severity: Int?,
        @SerializedName("StatusCode") var StatusCode: String?,
        @SerializedName("IssueDescription") var IssueDescription: String?,
        @SerializedName("DepartmentID") var DepartmentID: String?,
        @SerializedName("AssignedToUserID") var AssignedToUserID: String?,
        @SerializedName("StartTime") var StartTime: String?,
        @SerializedName("TargetEndTime") var TargetEndTime: String?,
        @SerializedName("ActualEndTime") var ActualEndTime: String?,
        @SerializedName("FixedVersion") var FixedVersion: String?,
        @SerializedName("EstimatedHours") var EstimatedHours: Double?,
        @SerializedName("CreatedBy") var CreatedBy: String?,
        @SerializedName("CreatedDate") var CreatedDate: String?,
        @SerializedName("ModifiedBy") var ModifiedBy: String?,
        @SerializedName("ModifiedDate") var ModifiedDate: String?,
        @SerializedName("AccountID") var AccountID: String?,
        @SerializedName("ProjectID") var ProjectID: String?,
        @SerializedName("ServiceRequestNo") var ServiceRequestNo: Int?,
        @SerializedName("IncidentID") var IncidentID: String?,
        @SerializedName("ComplaintID") var ComplaintID: Int?,
        @SerializedName("FirstName") var FirstName: String?,
        @SerializedName("LastName") var LastName: String?,
        @SerializedName("Email") var Email: String?,
        @SerializedName("Number") var Number: String?,
        @SerializedName("Address") var Address: String?,
        @SerializedName("Latitude") var Latitude: String?,
        @SerializedName("Longitude") var Longitude: String?,
        @SerializedName("CityID") var CityID: String?,
        @SerializedName("SectorID") var SectorID: String?,
        @SerializedName("ZoneID") var ZoneID: String?,
        @SerializedName("OldComplaintID") var OldComplaintID: String?,
        @SerializedName("Status") var Status: String?,
        @SerializedName("Incident") var Incident: String?,
        @SerializedName("AssignedTo") var AssignedTo: String?,
        @SerializedName("AssignedToEmail") var AssignedToEmail: String?,
        @SerializedName("AssignedToMobile") var AssignedToMobile: String?,
        @SerializedName("AssignedToPhone") var AssignedToPhone: String?,
        @SerializedName("Complaint") var Complaint: String?,
        @SerializedName("ComplaintNo") var ComplaintNo: Int?,
        @SerializedName("ComplaintDate") var ComplaintDate: String?,
        @SerializedName("AccountEmail") var AccountEmail: String?,
        @SerializedName("AccountMobile") var AccountMobile: String?,
        @SerializedName("AccountName") var AccountName: String?,
        @SerializedName("Sector") var Sector: String?,
        @SerializedName("City") var City: String?,
        @SerializedName("Zone") var Zone: String?

) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
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
            parcel.readValue(Double::class.java.classLoader) as? Double,
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
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(ContactID)
        parcel.writeString(ServiceRequestDate)
        parcel.writeValue(ProductCode)
        parcel.writeString(Title)
        parcel.writeValue(Category)
        parcel.writeValue(Priority)
        parcel.writeValue(Severity)
        parcel.writeString(StatusCode)
        parcel.writeString(IssueDescription)
        parcel.writeString(DepartmentID)
        parcel.writeString(AssignedToUserID)
        parcel.writeString(StartTime)
        parcel.writeString(TargetEndTime)
        parcel.writeString(ActualEndTime)
        parcel.writeString(FixedVersion)
        parcel.writeValue(EstimatedHours)
        parcel.writeString(CreatedBy)
        parcel.writeString(CreatedDate)
        parcel.writeString(ModifiedBy)
        parcel.writeString(ModifiedDate)
        parcel.writeString(AccountID)
        parcel.writeString(ProjectID)
        parcel.writeValue(ServiceRequestNo)
        parcel.writeString(IncidentID)
        parcel.writeValue(ComplaintID)
        parcel.writeString(FirstName)
        parcel.writeString(LastName)
        parcel.writeString(Email)
        parcel.writeString(Number)
        parcel.writeString(Address)
        parcel.writeString(Latitude)
        parcel.writeString(Longitude)
        parcel.writeString(CityID)
        parcel.writeString(SectorID)
        parcel.writeString(ZoneID)
        parcel.writeString(OldComplaintID)
        parcel.writeString(Status)
        parcel.writeString(Incident)
        parcel.writeString(AssignedTo)
        parcel.writeString(AssignedToEmail)
        parcel.writeString(AssignedToMobile)
        parcel.writeString(AssignedToPhone)
        parcel.writeString(Complaint)
        parcel.writeValue(ComplaintNo)
        parcel.writeString(ComplaintDate)
        parcel.writeString(AccountEmail)
        parcel.writeString(AccountMobile)
        parcel.writeString(AccountName)
        parcel.writeString(Sector)
        parcel.writeString(City)
        parcel.writeString(Zone)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VUCRMIncidentAndComplaint> {
        override fun createFromParcel(parcel: Parcel): VUCRMIncidentAndComplaint {
            return VUCRMIncidentAndComplaint(parcel)
        }

        override fun newArray(size: Int): Array<VUCRMIncidentAndComplaint?> {
            return arrayOfNulls(size)
        }
    }

}