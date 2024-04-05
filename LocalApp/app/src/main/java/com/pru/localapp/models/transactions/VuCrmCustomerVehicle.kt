package com.pru.localapp.models.transactions

import androidx.room.Embedded
import androidx.room.Relation
import com.pru.localapp.models.entities.CrmCustomer
import com.pru.localapp.models.entities.CrmCustomerVehicle

data class VuCrmCustomerVehicle(
    @Embedded
    val crmCustomerVehicle: CrmCustomerVehicle?,
    @Relation(
        parentColumn = "accountId",
        entityColumn = "customerId"
    )
    val crmCustomer: CrmCustomer?
)