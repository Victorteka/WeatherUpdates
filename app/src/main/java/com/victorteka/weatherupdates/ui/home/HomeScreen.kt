package com.victorteka.weatherupdates.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.victorteka.weatherupdates.R
import com.victorteka.weatherupdates.core.model.CurrentWeather
import com.victorteka.weatherupdates.core.model.HourlyWeather
import com.victorteka.weatherupdates.core.model.WeatherInfo
import com.victorteka.weatherupdates.getCityName


@Composable
fun HomeScreen(
    state: HomeScreenViewState,
    onTryAgainClicked: () -> Unit,
    onAddressReceived: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
    ) {
        state.weather?.let { weather ->
            LocalContext.current.getCityName(
                latitude = weather.latitude,
                longitude = weather.longitude,
            ) { address ->
                onAddressReceived(address.locality)
            }
        }
        HomeTopBar(cityName = state.cityName)
        if (state.isLoading) {
            Spacer(modifier = Modifier.weight(0.5f))
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.weight(0.5f))
        }
        if (state.error != null) {
            Spacer(modifier = Modifier.weight(0.5f))
            ErrorContent(
                errorId = state.error,
                modifier = Modifier.padding(8.dp)
            ) {
                onTryAgainClicked()
            }
            Spacer(modifier = Modifier.weight(0.5f))
        }
        state.weather?.currentWeather?.let { currentWeather ->
            CurrentWeatherWidget(currentWeather)
        }
        state.weather?.hourlyWeather?.let { hourlyWeather ->
            HourlyWeatherWidget(hourlyWeather)
        }
    }
}

@Composable
fun HourlyWeatherWidget(hourlyWeather: HourlyWeather) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ){
        Text(
            text = stringResource(R.string.home_today_forecast_title),
            style = MaterialTheme.typography.headlineSmall
        )
        for (weatherInfo in hourlyWeather.data) {
            HourlyWeatherRow(hourlyWeather = weatherInfo)
        }
    }
}

@Composable
fun HourlyWeatherRow(hourlyWeather: WeatherInfo) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ){
        Text(text = hourlyWeather.time, modifier = Modifier.padding(8.dp))
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = hourlyWeather.temperature,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun CurrentWeatherWidget(currentWeather: CurrentWeather) {
    Column {
        Text(
            text = stringResource(id = R.string.home_title_currently),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(vertical = 8.dp),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.tertiary
        )
        Text(
            text = currentWeather.temperature,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(vertical = 8.dp),
            style = MaterialTheme.typography.displayMedium
        )
    }
}

@Composable
fun HomeTopBar(cityName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = cityName,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun ErrorContent(
    errorId: Int,
    modifier: Modifier,
    onTryAgainClicked: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = errorId),
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.bodyMedium
        )
        Button(
            onClick = { onTryAgainClicked() },
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = stringResource(R.string.home_error_try_again),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}