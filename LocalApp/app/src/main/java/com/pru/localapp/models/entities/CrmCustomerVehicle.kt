package com.pru.localapp.models.entities

import androidx.room.*

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = CrmCustomer::class,
            parentColumns = ["customerId"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.SET_NULL
        ),
    ],
)
data class CrmCustomerVehicle(
    @PrimaryKey(autoGenerate = true) val customerVehicleId: Int? = null,
    val vehicleNo: String,
    val vehicleOwnerType: String,
    val vehicleType: String,
    @ColumnInfo(index = true)
    val accountId: Int
)
