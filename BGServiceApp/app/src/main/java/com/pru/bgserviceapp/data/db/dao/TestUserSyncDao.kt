package com.pru.bgserviceapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pru.bgserviceapp.data.db.entities.TestUserSync

@Dao
interface TestUserSyncDao {
    @Query("Select * from tbl_test_user_sync")
    suspend fun getAll(): List<TestUserSync>

    @Query("Select * from tbl_test_user_sync where `is-synced` = 'N'")
    suspend fun getUnSyncedUsers(): List<TestUserSync>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(users: List<TestUserSync>)
}