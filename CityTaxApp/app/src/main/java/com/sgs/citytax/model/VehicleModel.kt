package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class VehicleModel(
    @SerializedName("vehno")
    var VehicleNo: String? = null,
    @SerializedName("regno")
    var RegistrationNo: String? = null,
    @SerializedName("Transmission")
    var transmission: String? = "",
    @SerializedName("FuelType")
    var fuelType: String? = "",
    @SerializedName("RegistrationDate")
    var registrationDate: String? = "",
    @SerializedName("vehtypcode", alternate = ["VehicleTypeCode"])
    var vehicleTypeCode: String? = null,
    @SerializedName("stscode", alternate = ["StatusCode"])
    var statusCode: String? = null,
    var FileNameWithExtsn: String? = "",
    var FileData: String? = ""
)
