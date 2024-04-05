package com.pru.relationsdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pru.relationsdb.entities.VehicleOwner
import com.pru.relationsdb.views.VuVehicleOwner
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleOwnerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(vehicleOwner: VehicleOwner) : Long

    @Query("Select * from VuVehicleOwner where ownerId = :id")
    fun getVehiclesByOwners(id : Int) : Flow<List<VuVehicleOwner>>

    @Query("Select * from VehicleOwner")
    fun getVehicles() : Flow<List<VehicleOwner>>
}