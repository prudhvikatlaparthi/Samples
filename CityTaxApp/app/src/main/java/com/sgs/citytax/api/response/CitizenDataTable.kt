package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class CitizenDataTable(
    @SerializedName("Number")
    var Number: String? = null,
    @SerializedName("PhoneNumbers")
    var PhoneNumbers: String? = null,
    @SerializedName("acctid")
    var acctid: Int? = null,
    @SerializedName("acctname")
    var acctname: String? = null,
    @SerializedName("accttypcode")
    var accttypcode: String? = null,
    @SerializedName("GeoAddressID")
    var GeoAddressID: Int? = null,
    @SerializedName("cntrycode")
    var cntrycode: String? = null,
    @SerializedName("cntry")
    var cntry: String? = null,
    @SerializedName("st")
    var st: String? = null,
    @SerializedName("cty")
    var cty: String? = null,
    @SerializedName("zn")
    var zn: String? = null,
    @SerializedName("SectorID")
    var SectorID: Int? = null,
    @SerializedName("sec")
    var sec: String? = null,
    @SerializedName("Street")
    var Street: String? = null,
    @SerializedName("zip")
    var zip: String? = null,
    @SerializedName("Section")
    var Section: String? = null,
    @SerializedName("lot")
    var lot: String? = null,
    @SerializedName("Parcel")
    var Parcel: String? = null,
    @SerializedName("lat")
    var lat: String? = null,
    @SerializedName("long")
    var long: String? = null,
    @SerializedName("email")
    var email: String? = null,
    @SerializedName("CitizenID")
    var CitizenID: String? = null,
    @SerializedName("conname")
    var conname: String? = null,
    @SerializedName("conid")
    var conid: Int? = null,
    @SerializedName("CitizenSycotaxID")
    var CitizenSycotaxID: String? = null,
    @SerializedName("ctyid")
    var ctyid: Int? = null,
    @SerializedName("znid")
    var znid: Int? = null,
)