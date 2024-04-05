package com.pru.utils

object DBUtils {
    const val vuVehicleOwner =
        """SELECT V.vehicleNo,V.brand,V.manufacturingYear,O.ownerName,O.ownerMobile ,O.ownerId,O.vehicleNo as OwnerVehcNo
        FROM Vehicle V INNER JOIN VehicleOwner O ON V.vehicleNo = O.vehicleNo"""
}