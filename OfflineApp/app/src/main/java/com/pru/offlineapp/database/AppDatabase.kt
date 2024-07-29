package com.pru.offlineapp.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pru.offlineapp.MyApp
import com.pru.offlineapp.daos.PropertyDao
import com.pru.offlineapp.database.AppDatabase.Companion.TBL_PROPERTY
import com.pru.offlineapp.database.migrations.MIGRATION_1_2
import com.pru.offlineapp.entities.PropertyE

@Database(entities = [PropertyE::class], version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun propertyDao(): PropertyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    MyApp.context.applicationContext,
                    AppDatabase::class.java,
                    MyApp.context.applicationContext.packageName.replace(".", "_") + "_database"
                )
                    .addMigrations(MIGRATION_1_2).build()
                INSTANCE = instance
                instance
            }
        }

        const val TBL_PROPERTY = "tbl_property"
    }

}

enum class Tables(val tableName: String) {
    TblProperty(TBL_PROPERTY)
}