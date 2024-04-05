package com.pru.misc.di

import com.pru.misc.remote.APIService
import com.pru.misc.repository.APIRepository
import com.pru.misc.repository.APIRepositorySDK
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideHttpClientAPI(): HttpClient {
        return HttpClient(Android) {
            engine {
                connectTimeout = 100_000
                socketTimeout = 100_000
            }
            install(Logging) {
                level = LogLevel.ALL
            }
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
        }
    }

    @Provides
    @Singleton
    fun provideAPIService(client: HttpClient): APIService {
        return APIService(client)
    }

    @Provides
    @Singleton
    fun provideAPIRepositorySDK(apiService: APIService): APIRepository {
        return APIRepositorySDK(apiService)
    }
}