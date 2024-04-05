package com.pru.localapp.di

import com.pru.localapp.dao.entitydao.CrmCustomerDao
import com.pru.localapp.dao.entitydao.CrmCustomerVehicleDao
import com.pru.localapp.dao.transactiondao.VuCrmCustomerDao
import com.pru.localapp.dao.transactiondao.VuCrmCustomerVehicleDao
import com.pru.localapp.dao.viewdao.ViewCrmCustomerDao
import com.pru.localapp.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
object ActivityModule {
    @Provides
    @ActivityRetainedScoped
    fun provideCrmCustomerDao(appDatabase: AppDatabase): CrmCustomerDao =
        appDatabase.crmCustomerDao()

    @Provides
    @ActivityRetainedScoped
    fun provideViewCrmCustomerDao(appDatabase: AppDatabase): ViewCrmCustomerDao =
        appDatabase.viewCrmCustomerDao()

    @Provides
    @ActivityRetainedScoped
    fun provideCrmCustomerVehicleDao(appDatabase: AppDatabase): CrmCustomerVehicleDao =
        appDatabase.crmCustomerVehicleDao()

    @Provides
    @ActivityRetainedScoped
    fun provideVuCrmCustomerVehicleDao(appDatabase: AppDatabase): VuCrmCustomerVehicleDao =
        appDatabase.vuCrmCustomerVehicleDao()

    @Provides
    @ActivityRetainedScoped
    fun provideVuCrmCustomerDao(appDatabase: AppDatabase): VuCrmCustomerDao =
        appDatabase.vuCrmCustomerDao()
}