package com.victorteka.weatherupdates.di

import com.victorteka.weatherupdates.data.PollingService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    internal fun providePollingService() = PollingService
}