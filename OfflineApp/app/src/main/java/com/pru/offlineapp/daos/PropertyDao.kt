package com.pru.offlineapp.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pru.offlineapp.database.AppDatabase
import com.pru.offlineapp.entities.PropertyE

@Dao
interface PropertyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(propertyE: PropertyE) : Long

    @Query("Select * from ${AppDatabase.TBL_PROPERTY} ")
    fun getProperties() : LiveData<List<PropertyE>>

    @Query("Select * from ${AppDatabase.TBL_PROPERTY} where modifiedFrom = 'A'")
    suspend fun getPropertiesToUpload() : List<PropertyE>

    @Delete(entity = PropertyE::class)
    suspend fun deleteProperty(propertyE: PropertyE)

}