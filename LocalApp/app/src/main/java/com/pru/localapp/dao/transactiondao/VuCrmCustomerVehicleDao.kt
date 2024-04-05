package com.pru.localapp.dao.transactiondao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pru.localapp.models.transactions.VuCrmCustomerVehicle

@Dao
interface VuCrmCustomerVehicleDao {
    @Transaction
    @Query("Select * from CrmCustomerVehicle")
    fun getCustomerVehicles(): LiveData<List<VuCrmCustomerVehicle>>
}