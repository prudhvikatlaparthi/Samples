package com.pru.hiltarchi.di

import android.util.Log
import com.pru.hiltarchi.remote.APIRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.util.*
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object KtorHttpModule {

    @Singleton
    @Provides
    fun provideKTORHttpClient(): HttpClient = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = KotlinxSerializer(Json)
        }

        install(Logging) {
            logger = CustomHttpLogger()
            level = LogLevel.BODY
        }
    }

    @KtorExperimentalAPI
    @Singleton
    @Provides
    fun provideAPIService(httpClient: HttpClient): APIRepository = APIRepository(httpClient)

}

class CustomHttpLogger : Logger {
    override fun log(message: String) {
        Log.d("HttpLog", message)
    }
}