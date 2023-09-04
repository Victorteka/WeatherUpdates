package com.victorteka.weatherupdates

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.victorteka.weatherupdates.ui.home.HomeScreenIntent
import com.victorteka.weatherupdates.ui.home.HomeViewModel
import com.victorteka.weatherupdates.ui.theme.WeatherUpdatesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherUpdatesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherAppDestinationsConfig(
                        navController = rememberNavController(),
                        homeViewModel = homeViewModel
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        homeViewModel.processIntent(HomeScreenIntent.LoadWeatherData)
    }

    override fun onStop() {
        super.onStop()
        homeViewModel.processIntent(HomeScreenIntent.CancelWeatherDataPolling)
    }
}
