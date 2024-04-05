package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class CitizenModel(
    @SerializedName("lastname")
    var lastName: String? = "",
    @SerializedName("ph")
    var phone: String? = "",
    @SerializedName("telcode")
    var TelephoneCode: Int? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("GeoAddress")
    val geoAddress: GeoAddress? = null,
    var FileNameWithExtsn: String? = "",
    var FileData: String? = ""
)
