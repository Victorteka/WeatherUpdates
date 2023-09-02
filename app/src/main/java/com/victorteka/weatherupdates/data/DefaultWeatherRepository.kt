package com.victorteka.weatherupdates.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.victorteka.weatherupdates.core.api.WeatherRepository
import com.victorteka.weatherupdates.core.model.Weather
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED
import javax.inject.Inject

class DefaultWeatherRepository @Inject constructor(
    private val openMeteoService: OpenMeteoService,
    private val pollingService: PollingService
): WeatherRepository {

    private val TEN_SECONDS = 10_000L
    @RequiresApi(Build.VERSION_CODES.O)
    override fun fetchWeatherData(): Flow<Result<Weather>> = flow {
        pollingService.startPolling()
        var currentWeatherCoordinatesIndex = 0
        while (pollingService.isPolling()){
            val coordinate = coordinates[currentWeatherCoordinatesIndex]
            fetchFromApi(coordinate)
            if (currentWeatherCoordinatesIndex == coordinates.lastIndex) {
                currentWeatherCoordinatesIndex = 0
            } else {
                currentWeatherCoordinatesIndex += 1
            }

            delay(TEN_SECONDS)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun FlowCollector<Result<Weather>>.fetchFromApi(coordinate: Coordinate) {
        val response = openMeteoService.getWeatherData(
            latitude = coordinate.latitude,
            longitude = coordinate.longitude
        )
        if (response.isSuccessful && response.body() != null){
            val weatherData = response.body()!!.toCoreModel()
            emit(Result.Success(data = weatherData))
        } else {
            pollingService.stopPolling()
            val errorType = mapResponseCodeToErrorType(response.code())
            emit(Result.Error(errorType))
        }
    }

    private fun mapResponseCodeToErrorType(responseCode: Int): ErrorType {
        return when (responseCode) {
            HTTP_UNAUTHORIZED -> ErrorType.UNAUTHORIZED
            in 400..499 -> ErrorType.CLIENT
            in 500..600 -> ErrorType.SERVER
            else -> ErrorType.GENERIC
        }
    }
}