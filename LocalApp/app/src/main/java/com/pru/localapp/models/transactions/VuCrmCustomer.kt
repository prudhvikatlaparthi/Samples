package com.pru.localapp.models.transactions

import androidx.room.Embedded
import androidx.room.Relation
import com.pru.localapp.models.entities.CrmCustomer
import com.pru.localapp.models.entities.CrmCustomerVehicle

data class VuCrmCustomer(
    @Embedded
    val crmCustomer: CrmCustomer?,
    @Relation(
        parentColumn = "customerId",
        entityColumn = "accountId"
    )
    val crmCustomerVehicle: CrmCustomerVehicle?,
)