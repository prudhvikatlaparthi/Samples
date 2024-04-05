package com.pru.localapp.dao.transactiondao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pru.localapp.models.transactions.VuCrmCustomer
import com.pru.localapp.models.transactions.VuCrmCustomerVehicle

@Dao
interface VuCrmCustomerDao {
    @Transaction
    @Query("SELECT * FROM CrmCustomer")
    fun getCustomers(): LiveData<List<VuCrmCustomer>>
}