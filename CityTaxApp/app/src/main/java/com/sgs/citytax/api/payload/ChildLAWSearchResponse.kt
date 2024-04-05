package com.sgs.citytax.api.payload

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LAWViolationTypeS(
        @SerializedName("ViolationType")
        var violationType: String? = null
): Parcelable {
    override fun toString(): String {
        return violationType ?:""
    }
}

@Parcelize
data class VULAWViolationSubType(
        @SerializedName("ViolationType")
        var violationSubType:String?=null
): Parcelable{
    override fun toString(): String {
        return violationSubType?:""
    }
}

@Parcelize
data class VULAWImpoundmentType(
        @SerializedName("ImpoundmentType")
        var impoundmentType:String?=null
): Parcelable{
    override fun toString(): String {
        return impoundmentType?:""
    }
}

@Parcelize
data class VULAWImpoundmentSubType(
        @SerializedName("ImpoundmentSubType")
        var impoundmentSubType:String?=null
): Parcelable{
    override fun toString(): String {
        return impoundmentSubType?:""
    }
}