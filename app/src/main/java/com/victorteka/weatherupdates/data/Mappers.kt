package com.victorteka.weatherupdates.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.victorteka.weatherupdates.core.model.CurrentWeather
import com.victorteka.weatherupdates.core.model.HourlyWeather
import com.victorteka.weatherupdates.core.model.Units
import com.victorteka.weatherupdates.core.model.Weather
import com.victorteka.weatherupdates.core.model.WeatherInfo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
fun WeatherResponse.toCoreModel(): Weather = Weather(
    latitude = latitude,
    longitude = longitude,
    hourlyWeather = mapDatesToHourly(hourly),
    currentWeather = mapToCurrentWeather(currentWeather)
)

@RequiresApi(Build.VERSION_CODES.O)
private fun mapDatesToHourly(hourly: HourlyResponse): HourlyWeather  {
    val weatherInfoList = mutableListOf<WeatherInfo>()
    for ((index, time) in hourly.times.withIndex()) {
        val temperature = hourly.temperatures[index]
        val formattedTemperature =
            formatTemperatureValue(temperature = temperature, unit = Units.METRIC.value)
        val formattedTime = formattedDateToHourlyTime(time)
        val weatherInfo = WeatherInfo(time = formattedTime, temperature = formattedTemperature)
        weatherInfoList.add(weatherInfo)
    }
    return HourlyWeather(data = weatherInfoList)
}

private fun mapToCurrentWeather(currentWeather: CurrentWeatherResponse): CurrentWeather =
    CurrentWeather(
        temperature = formatTemperatureValue(
            temperature = currentWeather.temperature,
            unit = Units.METRIC.value
        )
    )

@RequiresApi(Build.VERSION_CODES.O)
fun formattedDateToHourlyTime(time: String): String {
    val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
    val outputFormat = DateTimeFormatter.ofPattern("HH:mm")

    val dateTime: LocalDateTime = LocalDateTime.parse(time, inputFormat)
    return dateTime.format(outputFormat)
}

private fun formatTemperatureValue(temperature: Float, unit: String): String =
    "${temperature.roundToInt()}${getUnitSymbols(unit = unit)}"

private fun getUnitSymbols(unit: String) = when (unit) {
    Units.METRIC.value -> Units.METRIC.tempLabel
    Units.IMPERIAL.value -> Units.IMPERIAL.tempLabel
    else -> "N/A"
}