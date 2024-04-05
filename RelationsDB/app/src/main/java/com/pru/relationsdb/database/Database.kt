package com.pru.relationsdb.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.pru.relationsdb.dao.VehicleDao
import com.pru.relationsdb.dao.VehicleOwnerDao
import com.pru.relationsdb.entities.Vehicle
import com.pru.relationsdb.entities.VehicleOwner
import com.pru.relationsdb.views.VuVehicleOwner

@Database(
    entities = [Vehicle::class, VehicleOwner::class], views = [VuVehicleOwner::class], version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun vehicleOwnerDao(): VehicleOwnerDao
}
