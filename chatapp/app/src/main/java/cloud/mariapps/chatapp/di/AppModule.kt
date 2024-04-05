package cloud.mariapps.chatapp.di

import cloud.mariapps.chatapp.navigation.AppController
import cloud.mariapps.chatapp.navigation.AppControllerSdk
import cloud.mariapps.chatapp.remote.ApiService
import cloud.mariapps.chatapp.repository.ApiRepository
import cloud.mariapps.chatapp.repository.ApiRepositorySdk
import cloud.mariapps.chatapp.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppController(): AppController = AppControllerSdk()

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder().baseUrl(Constants.kBaseUrl).client(
        OkHttpClient.Builder().addInterceptor(
            interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        ).build()
    ).addConverterFactory(
        GsonConverterFactory.create()
    ).build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideApiRepository(apiService: ApiService): ApiRepository = ApiRepositorySdk(apiService)
}