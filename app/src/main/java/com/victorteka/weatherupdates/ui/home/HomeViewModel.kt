package com.victorteka.weatherupdates.ui.home

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.victorteka.weatherupdates.R
import com.victorteka.weatherupdates.core.api.WeatherRepository
import com.victorteka.weatherupdates.core.model.Weather
import com.victorteka.weatherupdates.data.ErrorType
import com.victorteka.weatherupdates.data.PollingService
import com.victorteka.weatherupdates.data.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val pollingService: PollingService
): ViewModel() {

    private val _state = MutableStateFlow(HomeScreenViewState(isLoading = true))
    val state: StateFlow<HomeScreenViewState> = _state.asStateFlow()

    fun processIntent(homeScreenIntent: HomeScreenIntent) {
        when(homeScreenIntent){
            is HomeScreenIntent.LoadWeatherData ->{
                viewModelScope.launch {
                    weatherRepository.fetchWeatherData().collect { result ->
                        processResult(result)
                    }
                }
            }
            is HomeScreenIntent.CancelWeatherDataPolling -> {
                pollingService.startPolling()
            }
            is HomeScreenIntent.DisplayCityName -> {
                setState {
                    copy(cityName = homeScreenIntent.cityName)
                }
            }
        }
    }

    private fun processResult(result: Result<Weather>) {
        when(result) {
            is Result.Success -> {
                val weatherData = result.data
                setState {
                    copy(
                        weather = weatherData,
                        isLoading = false,
                        error = null
                    )
                }
            }
            is Result.Error -> {
                setState {
                    copy(
                        isLoading = false,
                        error = mapErrorTypeToResource(result.errorType)
                    )
                }
            }
        }
    }

    private fun mapErrorTypeToResource(errorType: ErrorType): Int = when(errorType) {
        ErrorType.GENERIC -> R.string.error_generic
        ErrorType.IO_CONNECTION -> R.string.error_client
        ErrorType.UNAUTHORIZED -> R.string.error_unauthorized
        ErrorType.CLIENT -> R.string.error_client
        ErrorType.SERVER -> R.string.error_server
    }

    private fun setState(stateReducer: HomeScreenViewState.() -> HomeScreenViewState) {
        viewModelScope.launch {
            _state.emit(stateReducer(state.value))
        }
    }
}

data class HomeScreenViewState(
    val cityName: String = "-",
    val weather: Weather? = null,
    val isLoading: Boolean = false,
    @StringRes val error: Int? = null
)