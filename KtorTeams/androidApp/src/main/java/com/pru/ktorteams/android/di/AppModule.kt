package com.pru.ktorteams.android.di

import com.pru.ktorteams.android.demo.Remote
import com.pru.ktorteams.android.demo.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideRemote() : Remote = Remote()


    @Provides
    @Singleton
    fun provideRepository(remote: Remote) : Repository = Repository(remote)

}