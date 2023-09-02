package com.victorteka.weatherupdates.di

import com.victorteka.weatherupdates.core.api.WeatherRepository
import com.victorteka.weatherupdates.data.DefaultWeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface RepositoryModule {

    @Binds
    fun bindWeatherRepository(weatherRepository: DefaultWeatherRepository): WeatherRepository
}