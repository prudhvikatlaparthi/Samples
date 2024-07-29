package com.pru.offlineapp.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pru.offlineapp.database.Tables


val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE ${Tables.TblProperty.tableName} ADD COLUMN address TEXT")
    }
}