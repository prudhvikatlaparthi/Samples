package com.pru.bgserviceapp.data.db.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pru.bgserviceapp.data.db.dao.TestUserSyncDao
import com.pru.bgserviceapp.data.db.entities.TestUserSync

@Database(entities = [TestUserSync::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun testUserSyncDao(): TestUserSyncDao
}