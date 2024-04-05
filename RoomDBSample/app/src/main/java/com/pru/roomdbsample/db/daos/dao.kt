package com.pru.roomdbsample.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pru.roomdbsample.db.entities.BasicDetailsEntity
import com.pru.roomdbsample.db.entities.ControlEntity
import com.pru.roomdbsample.db.entities.LayoutEntity

@Dao
interface BasicDetailsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBasicDetails(basicDetails: BasicDetailsEntity): Long

    @Query("SELECT * FROM basic_details")
    suspend fun getAllBasicDetails(): List<BasicDetailsEntity>
}

@Dao
interface LayoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLayout(layout: LayoutEntity): Long

    @Query("SELECT * FROM layout")
    suspend fun getAllLayouts(): List<LayoutEntity>
}

@Dao
interface ControlDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertControl(control: ControlEntity): Long

    @Query("SELECT * FROM control")
    suspend fun getAllControls(): List<ControlEntity>
}