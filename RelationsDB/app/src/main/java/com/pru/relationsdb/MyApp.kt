package com.pru.relationsdb

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pru.relationsdb.database.AppDatabase
import com.pru.utils.DBUtils

class MyApp : Application() {
    companion object {
        lateinit var database: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "my-database"
        ).addMigrations(MIGRATION_1_2).build()
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP VIEW `VuVehicleOwner`")
        database.execSQL("""CREATE VIEW `VuVehicleOwner` AS ${DBUtils.vuVehicleOwner}""")
    }
}
