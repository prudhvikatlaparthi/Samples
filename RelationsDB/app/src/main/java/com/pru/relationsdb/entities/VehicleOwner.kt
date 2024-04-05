package com.pru.relationsdb.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = Vehicle::class,
        parentColumns = arrayOf("vehicleNo"),
        childColumns = arrayOf("vehicleNo"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class VehicleOwner(
    @PrimaryKey(autoGenerate = true) var ownerId: Int? = null,
    var ownerName: String,
    var ownerMobile: String,
    var vehicleNo: String
)
