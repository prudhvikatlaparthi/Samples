package com.sgs.citytax.api.payload

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal


data class NewServiceRequest(
        @SerializedName("svcreqdt")
        var serviceRequestDate: String? = null,
        @SerializedName("stscode")
        var statusCode: String? = null,
        @SerializedName("issdesc")
        var description: String? = null,
        @SerializedName("assgnd2usrid")
        var assignToUserID: String? = null,
        @SerializedName("long")
        var longitude: Double? = 0.0,
        @SerializedName("lat")
        var latitude: Double? = 0.0,
        @SerializedName("ParentServiceRequestNo")
        var parentServiceRequestNo: String? = "",
        @SerializedName("strttime")
        var startTime: String? = "",
        @SerializedName("tgtendtime")
        var targetEndTime: String? = "",
        @SerializedName("AssignTime")
        var assignTime: String? = "",
        @SerializedName("ClosedTime")
        var closedTime: String? = "",
        @SerializedName("svcreqno")
        var serviceRequestNo: String? = "",
        @SerializedName("acctid")
        var accountID: String? = null,
        @SerializedName("incidentID")
        var incidentID: Int? = 0,
        @SerializedName("IncidentSubtypeID")
        var incidentSubtypeID: Int? = 0,
        @SerializedName("ctyid")
        var cityID: Int? = null,
        @SerializedName("SectorID")
        var sectorID: Int? = null,
        @SerializedName("znid")
        var zoneID: Int? = null,
        @SerializedName("ServiceTypeID")
        var serviceTypeID: Int? = null,
        @SerializedName("ServiceSubTypeID")
        var serviceSubTypeID: Int? = null,
        @SerializedName("area")
        var area: String? = null,
        @SerializedName("AdvanceAmount")
        var advanceAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("EstimatedAmount")
        var estimatedAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("cntrycode")
        var countryCode: String? = null,
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
        @SerializedName("stid")
        var stateID: Int? = null,
        @Expose(serialize = false, deserialize = false)
        var isLoading: Boolean = false,
        @SerializedName("ServiceType")
        var serviceType: String? = null,
        @SerializedName("ServiceSubType")
        var serviceSubType: String? = null,
        @SerializedName("acctname")
        var customer: String? = null,
        @SerializedName("sts")
        var status: String? = null,
        @SerializedName("unitcode")
        var unitcode: String? = null,
        @SerializedName("PricingRuleID")
        var pricingRuleID: Int? = null,
        @SerializedName("TentativeRequestedDate")
        var serviceDate: String? = null,
        @SerializedName("AssignTo3rdParty")
        var assignTo3rdParty: String? = null,
        @SerializedName("asnthrdprty")
        var is3rdParty: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
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
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readSerializable() as BigDecimal?,
            parcel.readSerializable() as BigDecimal?,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readByte() != 0.toByte(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString()){
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(serviceRequestDate)
        parcel.writeString(statusCode)
        parcel.writeString(description)
        parcel.writeString(assignToUserID)
        parcel.writeValue(longitude)
        parcel.writeValue(latitude)
        parcel.writeString(parentServiceRequestNo)
        parcel.writeString(startTime)
        parcel.writeString(targetEndTime)
        parcel.writeString(assignTime)
        parcel.writeString(closedTime)
        parcel.writeString(serviceRequestNo)
        parcel.writeString(accountID)
        parcel.writeValue(incidentID)
        parcel.writeValue(incidentSubtypeID)
        parcel.writeValue(cityID)
        parcel.writeValue(sectorID)
        parcel.writeValue(zoneID)
        parcel.writeValue(serviceTypeID)
        parcel.writeValue(serviceSubTypeID)
        parcel.writeString(area)
        parcel.writeString(countryCode)
        parcel.writeString(street)
        parcel.writeString(zip)
        parcel.writeString(plot)
        parcel.writeString(block)
        parcel.writeString(doorNo)
        parcel.writeValue(stateID)
        parcel.writeByte(if (isLoading) 1 else 0)
        parcel.writeString(serviceType)
        parcel.writeString(serviceSubType)
        parcel.writeString(customer)
        parcel.writeString(status)
        parcel.writeString(unitcode)
        parcel.writeValue(pricingRuleID)
        parcel.writeString(serviceDate)
        parcel.writeString(assignTo3rdParty)
        parcel.writeString(is3rdParty)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NewServiceRequest> {
        override fun createFromParcel(parcel: Parcel): NewServiceRequest {
            return NewServiceRequest(parcel)
        }

        override fun newArray(size: Int): Array<NewServiceRequest?> {
            return arrayOfNulls(size)
        }
    }
}