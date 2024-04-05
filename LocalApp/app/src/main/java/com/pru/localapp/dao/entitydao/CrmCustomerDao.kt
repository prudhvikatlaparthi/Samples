package com.pru.localapp.dao.entitydao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.pru.localapp.models.entities.CrmCustomer

@Dao
interface CrmCustomerDao {
    @Query("Select * from CrmCustomer")
    fun getCustomers(): LiveData<List<CrmCustomer>>

    @Insert
    fun insertAll(crmCustomer: List<CrmCustomer>) : List<Long>

}