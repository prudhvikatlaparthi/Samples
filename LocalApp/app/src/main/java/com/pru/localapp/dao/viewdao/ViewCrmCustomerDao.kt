package com.pru.localapp.dao.viewdao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.pru.localapp.models.entities.CrmCustomer
import com.pru.localapp.models.views.ViewCustomer

@Dao
interface ViewCrmCustomerDao {
    @Query("Select * from ViewCustomer")
    fun getCustomers(): LiveData<List<ViewCustomer>>
}