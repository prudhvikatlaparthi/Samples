package com.pru.shopping.androidApp.di

import com.pru.shopping.shared.commonRepositories.RepositorySDK
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRepositorySDK(): RepositorySDK = RepositorySDK()
}