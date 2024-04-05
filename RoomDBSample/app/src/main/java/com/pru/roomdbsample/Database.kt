package com.pru.roomdbsample

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pru.roomdbsample.db.daos.BasicDetailsDao
import com.pru.roomdbsample.db.daos.ControlDao
import com.pru.roomdbsample.db.daos.LayoutDao
import com.pru.roomdbsample.db.entities.BasicDetailsEntity
import com.pru.roomdbsample.db.entities.ControlEntity
import com.pru.roomdbsample.db.entities.LayoutEntity

@Database(
    entities = [BasicDetailsEntity::class, LayoutEntity::class, ControlEntity::class], version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun basicDetailsDao(): BasicDetailsDao
    abstract fun layoutDao(): LayoutDao
    abstract fun controlDao(): ControlDao
}