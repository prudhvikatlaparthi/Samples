package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.sgs.citytax.util.displayFormatDate
import java.math.BigDecimal

data class PropertyDetailLocation(
        @SerializedName("PropertySycotaxID")
        var PropertySycotaxID: String? = "",
        @SerializedName("GeoLocationArea")
        var GeoLocationArea: String? = "",
        @SerializedName("Zone")
        var Zone: String? = "",
        @SerializedName("Sector")
        var Sector: String? = "",
        @SerializedName("SurveyNo")
        var SurveyNo: String? = "",
        @SerializedName("ColorHex")
        var ColorHex: String? = "",
        @SerializedName("PropertyType")
        var PropertyType: String? = "",
        @SerializedName("RegistrationNo")
        var RegistrationNo: String? = null,
        @SerializedName("OverDueAmount")
        var OverDueAmount: Int? = null,
        @SerializedName("TaxDue")
        var TaxDue: BigDecimal? = null,
        @SerializedName("OnboardingDate")
        var OnboardingDate: String? = "",
        @SerializedName("OnboardingYear")
        var OnboardingYear: Int? = null,
        @SerializedName("OnboardingMonth")
        var OnboardingMonth: Int? = null,
        @SerializedName("TaxGeneratedCount")
        var TaxGeneratedCount: String? = null,
        @SerializedName("Color")
        var Color: String? = "",
        @SerializedName("Legend")
        var Legend: String? = "",
        @SerializedName("TaxRuleBookCode")
        var taxRuleBookCode: String? = "",
        @SerializedName("PropertyID")
        var propertyID: Int? = null,
        @SerializedName("PropertyVerificationRequestID")
        var propertyVerificationRequestID: Int? = null,
        @SerializedName("Owner")
        var owner: String? = "",
        @SerializedName("4rmdt")
        var fromDate: String? = "",
        @SerializedName("2dt")
        var toDate: String? = ""
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
            parcel.readValue(Int::class.java.classLoader) as? Int,
            TODO("TaxDue"),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString())


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(PropertySycotaxID)
        parcel.writeString(GeoLocationArea)
        parcel.writeString(Zone)
        parcel.writeString(Sector)
        parcel.writeString(SurveyNo)
        parcel.writeString(ColorHex)
        parcel.writeString(PropertyType)
        parcel.writeString(RegistrationNo)
        parcel.writeValue(OverDueAmount)
        parcel.writeString(OnboardingDate)
        parcel.writeValue(OnboardingYear)
        parcel.writeValue(OnboardingMonth)
        parcel.writeString(TaxGeneratedCount)
        parcel.writeString(Color)
        parcel.writeString(Legend)
        parcel.writeString(taxRuleBookCode)
        parcel.writeValue(propertyID)
        parcel.writeValue(propertyVerificationRequestID)
        parcel.writeString(owner)
        parcel.writeString(fromDate)
        parcel.writeString(toDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PropertyDetailLocation> {
        override fun createFromParcel(parcel: Parcel): PropertyDetailLocation {
            return PropertyDetailLocation(parcel)
        }

        override fun newArray(size: Int): Array<PropertyDetailLocation?> {
            return arrayOfNulls(size)
        }
    }


    fun onBoardingDate(): String {
        if (OnboardingDate != null) {
            return displayFormatDate(OnboardingDate)
        }
        return ""
    }
}