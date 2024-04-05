package com.pru.relationsdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pru.relationsdb.entities.Vehicle
import com.pru.relationsdb.views.VuVehicleOwner

@Dao
interface VehicleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(vehicle: Vehicle) : Long
}