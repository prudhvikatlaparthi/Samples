package com.pru.relationsdb.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Vehicle(
    @PrimaryKey(autoGenerate = false) var vehicleNo: String,
    var brand: String,
    var manufacturingYear: String
)
