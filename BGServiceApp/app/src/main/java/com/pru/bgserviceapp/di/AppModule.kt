package com.pru.bgserviceapp.di

import android.content.Context
import androidx.room.Room
import com.pru.bgserviceapp.data.db.dao.TestUserSyncDao
import com.pru.bgserviceapp.data.db.database.AppDatabase
import com.pru.bgserviceapp.data.db.repository.AppRepositorySdk
import com.pru.bgserviceapp.domain.repository.AppRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java, "app-database"
        ).build()

    @Provides
    @Singleton
    fun provideTestUserSyncDao(appDatabase: AppDatabase): TestUserSyncDao =
        appDatabase.testUserSyncDao()

    @Provides
    @Singleton
    fun provideAppRepository(appDatabase: AppDatabase): AppRepository = AppRepositorySdk(
        appDatabase = appDatabase
    )

}