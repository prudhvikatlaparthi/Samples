package com.pru.touchnote.di

import android.app.Application
import androidx.room.Room
import com.pru.newsapp.mvvm.db.UsersDao
import com.pru.newsapp.mvvm.db.UsersDatabase
import com.pru.newsapp.mvvm.db.UsersDatabase.Companion.MIGRATION_1_2
import com.pru.touchnote.remote.GoRestAPI
import com.pru.touchnote.utils.Constants.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(GoRestAPI.BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build()
            )
            .build()

    @Provides
    @Singleton
    fun provideGoRestApi(retrofit: Retrofit): GoRestAPI =
        retrofit.create(GoRestAPI::class.java)

    @Singleton
    @Provides
    fun provideAppDB(app: Application): UsersDatabase =
        Room.databaseBuilder(app, UsersDatabase::class.java, DATABASE_NAME)
            .addMigrations(MIGRATION_1_2)
            .build()
//            .fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideArticleDao(usersDatabase: UsersDatabase): UsersDao =
        usersDatabase.getUserDao()
}