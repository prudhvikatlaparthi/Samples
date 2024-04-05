package com.mindorks.framework.mvi.di

import com.mindorks.framework.mvi.data.api.ApiService
import com.mindorks.framework.mvi.util.getInterceptors
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.scopes.ActivityScoped
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofitBuilder() : Retrofit=
        Retrofit.Builder().baseUrl("https://5e510330f2c0d300147c034c.mockapi.io")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(getInterceptors())
                    .build()
            )
            .build()

    @Provides
    @Singleton
    fun providesRestApi(retrofit: Retrofit) : ApiService = retrofit.create(ApiService::class.java)


}