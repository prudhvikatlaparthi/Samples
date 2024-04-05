package com.sgs.citytax.api.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class COMZoneMasterS(
@SerializedName("zn")
var zone: String? = null
): Parcelable{
    override fun toString(): String {
        return zone ?:""
    }
}

@Parcelize
data class COMSector(
@SerializedName("sec")
var sector: String? = null
): Parcelable{
    override fun toString(): String {
        return sector ?:""
    }
}

@Parcelize
data class VUCRMTaxSubType(
@SerializedName("TaxSubType")
var TaxSubType: String? = null
): Parcelable{
    override fun toString(): String {
        return TaxSubType?:""
    }
}

@Parcelize
data class CRMActivityDomainS(
@SerializedName("ActivityDomain")
var activityDomain: String? = null
): Parcelable{
    override fun toString(): String {
        return activityDomain?:""
    }
}

@Parcelize
data class CRMActivityClassS(
@SerializedName("ActivityClass")
var activityClass: String? = null
): Parcelable{
    override fun toString(): String {
        return activityClass?:""
    }
}

@Parcelize
data class VUINVProducts(
@SerializedName("prod")
var TaxType: String? = null,
@SerializedName("prodcode")
var TaxTypeCode: String? = null
): Parcelable{
    override fun toString(): String {
        return TaxType?:""
    }
}