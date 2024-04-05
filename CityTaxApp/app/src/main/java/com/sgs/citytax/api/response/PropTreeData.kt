package com.example.treestructure

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.sgs.citytax.R
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.getString

class PropTreeData(
        @SerializedName("id")
        var id: Int? = null,
        @SerializedName("parent")
        var parent: Int? = null,
        @SerializedName("ParentID")
        var ParentID: String? = "",
        @SerializedName("SurveyNo")
        var SurveyNo: String? = "",
        @SerializedName("TopParent")
        var TopParent: Int? = null,
        @SerializedName("level")
        var level: Int? = null,
        @SerializedName("expanded")
        var expanded: Int? = null,
        @SerializedName("loaded")
        var loaded: Int? = null,
        @SerializedName("isLeaf")
        var isLeaf: Int? = null,
        @SerializedName("PropertyName")
        var PropertyName: String? = "",
        @SerializedName("PropertySycotaxID")
        var PropertySycotaxID: String? = "",
        @SerializedName("PropertyType")
        var PropertyType: String? = "",
        @SerializedName("MonthlyRentAmount")
        var MonthlyRentAmount: Double = 0.0,
        @SerializedName("EstimatedRentAmount")
        var EstimatedRentAmount: Double = 0.0,
        @SerializedName("CreatedDate")
        var CreatedDate: String? = "",
        @SerializedName("ElectricityConsumption")
        var ElectricityConsumption: String? = null,
        @SerializedName("PhaseOfElectricity")
        var PhaseOfElectricity: String? = null,
        @SerializedName("WaterConsumption")
        var WaterConsumption: String? = null,
        @SerializedName("ComfortLevel")
        var ComfortLevel: String? = null,
        @SerializedName("Area")
        var Area: Double = 0.0,
        @SerializedName("ConstructedDate")
        var ConstructedDate: String? = "",
        @SerializedName("Status")
        var Status: String? = "",
        @SerializedName("RegistrationNo")
        var RegistrationNo: String? = null,
        @SerializedName("TaxRuleBookCode")
        var TaxRuleBookCode: String? = null
):Parcelable{
        constructor(parcel: Parcel) : this(
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readString(),
                parcel.readString(),
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readDouble(),
                parcel.readDouble(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readDouble(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString()) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeValue(id)
                parcel.writeValue(parent)
                parcel.writeString(ParentID)
                parcel.writeString(SurveyNo)
                parcel.writeValue(TopParent)
                parcel.writeValue(level)
                parcel.writeValue(expanded)
                parcel.writeValue(loaded)
                parcel.writeValue(isLeaf)
                parcel.writeString(PropertyName)
                parcel.writeString(PropertySycotaxID)
                parcel.writeString(PropertyType)
                parcel.writeDouble(MonthlyRentAmount)
                parcel.writeDouble(EstimatedRentAmount)
                parcel.writeString(CreatedDate)
                parcel.writeString(ElectricityConsumption)
                parcel.writeString(PhaseOfElectricity)
                parcel.writeString(WaterConsumption)
                parcel.writeString(ComfortLevel)
                parcel.writeDouble(Area)
                parcel.writeString(ConstructedDate)
                parcel.writeString(Status)
                parcel.writeString(RegistrationNo)
                parcel.writeString(TaxRuleBookCode)

        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<PropTreeData> {
                override fun createFromParcel(parcel: Parcel): PropTreeData {
                        return PropTreeData(parcel)
                }

                override fun newArray(size: Int): Array<PropTreeData?> {
                        return arrayOfNulls(size)
                }
        }
        fun taxRuleBookCode(): Boolean {
        if(TaxRuleBookCode==Constant.TaxRuleBook.LAND_PROP.name){
                return true
        }
                return false
        }

        fun dynamicIDSycotaxString():String{
                if(taxRuleBookCode()){
                        return  getString(R.string.receipt_land_id_sycotax)
                }
                return  getString(R.string.receipt_property_id_sycotax)
        }

        fun dynamicPropertyNameString():String{
                if(taxRuleBookCode()){
                        return  getString(R.string.receipt_land_name_cap)
                }
                return  getString(R.string.receipt_property_name_cap)
        }

        fun dynamicPropertyTypeString():String{
                if(taxRuleBookCode()){
                        return  getString(R.string.receipt_land_type)
                }
                return  getString(R.string.receipt_property_type)
        }

}