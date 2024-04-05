package com.pru.localapp.di

import android.content.Context
import androidx.room.Room
import com.pru.localapp.db.AppDatabase
import com.pru.localapp.db.migrations.RoomMigration
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
    fun provideAppDatabase(@ApplicationContext applicationContext: Context): AppDatabase =
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, AppDatabase.databaseName)
            .addMigrations(RoomMigration(1, 2), RoomMigration(2, 3),RoomMigration(3, 4)).build()
}