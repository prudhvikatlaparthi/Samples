package com.sgs.citytax.api.payload

import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.VehicleMaster
import com.sgs.citytax.model.VehicleOwnership

data class GetInsertVehicleOwnershipDetails(
        var context: SecurityContext = SecurityContext(),
        var VehicleOwnership: VehicleOwnership,
        var VehicleMaster: VehicleMaster
)
