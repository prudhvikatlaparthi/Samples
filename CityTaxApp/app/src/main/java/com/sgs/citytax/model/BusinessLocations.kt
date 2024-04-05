package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import com.google.maps.android.clustering.ClusterItem
import java.math.BigDecimal

data class BusinessLocations(
        @SerializedName("Business")
        var business: String? = null,
        @SerializedName("Latitude")
        var latitude: String? = null,
        @SerializedName("Longitude")
        var longitude: String? = null,
        @SerializedName("SycotaxID")
        var sycotaxID: String? = null,
        @SerializedName("TaxDue")
        var taxDue: BigDecimal = BigDecimal.ZERO,
        @SerializedName("Sector")
        var sector: String? = null,
        @SerializedName("Zone")
        var zone: String? = null,
        @SerializedName("Email")
        var email: String? = null,
        @SerializedName("Phone")
        var phone: String? = null,
        @SerializedName("ActivityDomain")
        var activityDomain: String? = null,
        @SerializedName("ActivityClass")
        var activityClass: String? = null,
        @SerializedName("OnboardingDate")
        var onboardingDate: String? = null,
        @SerializedName("OnboardingYear")
        var onboardingYear: String? = null,
        @SerializedName("OnboardingMonth")
        var onboardingMonth: String? = null,
        @SerializedName("Color")
        var color: String? = null,
        @SerializedName("TaxSubTypes")
        var taxSubType:String?=null,
        @SerializedName("TaxTypes")
        var taxType:String?=null
) : Parcelable, ClusterItem {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(BigDecimal::class.java.classLoader) as BigDecimal,
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
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(business)
        parcel.writeString(latitude)
        parcel.writeString(longitude)
        parcel.writeString(sycotaxID)
        parcel.writeString(sector)
        parcel.writeString(zone)
        parcel.writeString(email)
        parcel.writeString(phone)
        parcel.writeString(activityDomain)
        parcel.writeString(activityClass)
        parcel.writeString(onboardingDate)
        parcel.writeString(onboardingYear)
        parcel.writeString(onboardingMonth)
        parcel.writeString(color)
        parcel.writeString(taxSubType)
        parcel.writeString(taxType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BusinessLocations> {
        override fun createFromParcel(parcel: Parcel): BusinessLocations {
            return BusinessLocations(parcel)
        }

        override fun newArray(size: Int): Array<BusinessLocations?> {
            return arrayOfNulls(size)
        }
    }

    override fun getPosition(): LatLng {
        val lat: Double? = this.latitude?.toDouble()
        val lng: Double? = this.longitude?.toDouble()
        return lat?.let { lng?.let { it1 -> LatLng(it, it1) } }!!
    }

    override fun getTitle(): String {
        return ""
    }

    override fun getSnippet(): String {
        return ""
    }
}