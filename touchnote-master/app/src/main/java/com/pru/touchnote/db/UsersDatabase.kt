package com.pru.newsapp.mvvm.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pru.touchnote.data.model.Data


@Database(
    entities = [Data::class],
    version = 2,
    exportSchema = false
)
abstract class UsersDatabase : RoomDatabase() {

    abstract fun getUserDao(): UsersDao

    companion object{
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Data ADD COLUMN check_test TEXT")
            }
        }
    }


}