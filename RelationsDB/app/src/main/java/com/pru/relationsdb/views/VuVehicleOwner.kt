package com.pru.relationsdb.views

import androidx.room.DatabaseView
import com.pru.utils.DBUtils

@DatabaseView(
    DBUtils.vuVehicleOwner
)
data class VuVehicleOwner(
    var vehicleNo: String,
    var brand: String,
    var manufacturingYear: String,
    var ownerName: String,
    var ownerMobile: String,
    var ownerId: Int,
    var OwnerVehcNo: String,
)