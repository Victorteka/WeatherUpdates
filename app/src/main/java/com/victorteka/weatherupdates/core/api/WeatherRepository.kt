package com.victorteka.weatherupdates.core.api

import com.victorteka.weatherupdates.core.model.Weather
import com.victorteka.weatherupdates.data.Result
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun fetchWeatherData(): Flow<Result<Weather>>
}