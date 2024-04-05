package com.pru.localapp.models.views

import androidx.room.DatabaseView
import androidx.room.PrimaryKey
import com.pru.localapp.models.entities.CrmCustomer
import com.pru.localapp.models.entities.CrmCustomerVehicle

@DatabaseView("Select c.* ,vm.* from CrmCustomer c inner join CrmCustomerVehicle vm On c.customerId==vm.accountId")
data class ViewCustomer(
    val customerId: Int,
    val name: String,
    val mobile: String,
    val email: String,
    val address: String? = null,
    val loyaltyPoints: Double = 0.0,
    val customerVehicleId: Int? = null,
    val vehicleNo: String,
    val vehicleOwnerType: String,
    val vehicleType: String
){
    var isActive : Boolean? = false
    var isSelected : Boolean? = false
}