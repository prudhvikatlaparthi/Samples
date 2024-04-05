package com.pru.newsapp.mvvm.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.pru.touchnote.data.model.Data
import io.reactivex.Completable

@Dao
interface UsersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upInsert(data: List<Data>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upInsertRXJ(data: List<Data>): Completable
}