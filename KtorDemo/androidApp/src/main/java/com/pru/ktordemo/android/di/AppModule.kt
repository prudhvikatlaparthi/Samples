package com.pru.ktordemo.android.di

import com.pru.ktordemo.android.demo.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSampleDependent(API: API): Repository = Repository(API = API)

    @Provides
    @Singleton
    fun providesSample(): API = API()

    @Provides
    @Singleton
    fun provideElectricEngine() : ElecticEngine = ElecticEngine()

    @Provides
    @Singleton
    fun providePetrolEngine() : PetrolEngine = PetrolEngine()

    @Provides
    @Singleton
    @Electric
    fun provideElectricCar(electicEngine: ElecticEngine): Car = Car(electicEngine)

    @Provides
    @Singleton
    @Petrol
    fun providePetrolCar(petrolEngine: PetrolEngine): Car = Car(petrolEngine)

}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Electric

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Petrol