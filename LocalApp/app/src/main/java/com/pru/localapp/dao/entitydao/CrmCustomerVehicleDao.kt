package com.pru.localapp.dao.entitydao

import androidx.room.*
import com.pru.localapp.models.entities.CrmCustomerVehicle

@Dao
interface CrmCustomerVehicleDao {
    @Query("Select * from CrmCustomerVehicle")
    fun getCustomerVehicles(): List<CrmCustomerVehicle>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(crmCustomerVehicle: List<CrmCustomerVehicle>) : List<Long>
}