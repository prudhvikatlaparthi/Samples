package com.pru.localapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pru.localapp.dao.entitydao.CrmCustomerDao
import com.pru.localapp.dao.entitydao.CrmCustomerVehicleDao
import com.pru.localapp.dao.transactiondao.VuCrmCustomerDao
import com.pru.localapp.dao.transactiondao.VuCrmCustomerVehicleDao
import com.pru.localapp.dao.viewdao.ViewCrmCustomerDao
import com.pru.localapp.models.entities.CrmCustomer
import com.pru.localapp.models.entities.CrmCustomerVehicle
import com.pru.localapp.models.views.ViewCustomer

@Database(
    entities = [CrmCustomer::class, CrmCustomerVehicle::class],
    version = 4,
    exportSchema = false,
    views = [ViewCustomer::class]
)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        const val databaseName = "Local-App"
    }

    abstract fun crmCustomerDao(): CrmCustomerDao

    abstract fun viewCrmCustomerDao(): ViewCrmCustomerDao

    abstract fun crmCustomerVehicleDao(): CrmCustomerVehicleDao

    abstract fun vuCrmCustomerVehicleDao(): VuCrmCustomerVehicleDao

    abstract fun vuCrmCustomerDao(): VuCrmCustomerDao
}