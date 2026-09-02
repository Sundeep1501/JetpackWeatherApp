package com.sundeep1501.weather

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sundeep1501.weather.ui.screens.search.SearchScreen
import com.sundeep1501.weather.ui.screens.weather.HomeScreen
import com.sundeep1501.weather.ui.screens.weather.HomeViewModel
import com.sundeep1501.weather.ui.theme.WeatherTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val controller = rememberNavController()
                    val homeViewModel: HomeViewModel = hiltViewModel()

                    val permissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission()
                    ) { isGranted ->
                        if (isGranted) {
                            homeViewModel.fetchCurrentLocationWeather()
                        }
                    }

                    LaunchedEffect(Unit) {
                        if (ContextCompat.checkSelfPermission(
                                this@MainActivity,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            homeViewModel.fetchCurrentLocationWeather()
                        } else {
                            permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                        }
                    }

                    NavHost(
                        controller,
                        startDestination = Destination.WeatherDetails
                    ) {
                        composable<Destination.Search> {
                            SearchScreen(modifier = Modifier.padding(innerPadding)) { city ->
                                controller.popBackStack()
                                homeViewModel.citySelected(city)
                            }
                        }
                        composable<Destination.WeatherDetails> {
                            HomeScreen(
                                modifier = Modifier.padding(innerPadding),
                                viewModel = homeViewModel
                            ) {
                                controller.navigate(Destination.Search)
                            }
                        }
                    }
                }
            }
        }

    }

    interface Destination {
        @Serializable
        object Search : Destination

        @Serializable
        object WeatherDetails : Destination
    }
}