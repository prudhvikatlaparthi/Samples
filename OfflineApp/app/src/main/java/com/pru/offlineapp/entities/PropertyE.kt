package com.pru.offlineapp.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pru.offlineapp.database.AppDatabase

@Entity(tableName = AppDatabase.TBL_PROPERTY)
data class PropertyE(
    @PrimaryKey(autoGenerate = true) var propertyID: Int? = null,
    var propertyName: String,
    var propertyArea: Double,
    var webPropertyID: Int,
    var modifiedFrom: String,
    var address : String? = null
)
