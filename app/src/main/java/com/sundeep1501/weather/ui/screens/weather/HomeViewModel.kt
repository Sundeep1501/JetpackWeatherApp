package com.sundeep1501.weather.ui.screens.weather

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.sundeep1501.weather.data.WeatherRepository
import com.sundeep1501.weather.data.local.DataStoreManager
import com.sundeep1501.weather.data.models.City
import com.sundeep1501.weather.data.models.WeatherResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val dataStoreManager: DataStoreManager,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) :
    ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _selectedCity = MutableStateFlow(DEFAULT_CITY)
    val selectedCity = _selectedCity.asStateFlow()

    init {
        loadLastLocation()
    }

    private fun loadLastLocation() {
        viewModelScope.launch {
            val lastCity = dataStoreManager.getLastCity().first()
            citySelected(lastCity ?: DEFAULT_CITY)
        }
    }

    @SuppressLint("MissingPermission")
    fun fetchCurrentLocationWeather() {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            try {
                val location = fusedLocationProviderClient.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    CancellationTokenSource().token
                ).await()

                if (location != null) {
                    val city = City(
                        name = "--",
                        lat = location.latitude,
                        lon = location.longitude,
                        country = "--",
                        state = "--"
                    )
                    citySelected(city)
                } else {
                    loadLastLocation()
                }
            } catch (e: Exception) {
                Log.e(HomeViewModel::class.java.name, "fetchCurrentLocationWeather, Exception: ${e.message}")
                loadLastLocation()
            }
        }
    }

    fun citySelected(city: City) {
        _selectedCity.value = city
        fetchWeather(city.lat, city.lon)
    }

    private fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val weatherResponse = weatherRepository.getWeather(lat, lon)
                handleResponse(weatherResponse, lat, lon)
            } catch (ex: CancellationException) {
                throw ex
            } catch (ex: Exception) {
                Log.e(HomeViewModel::class.java.name, "fetchWeather, Exception: ${ex.message}")
                handleError()
            }
        }
    }

    fun handleError() {
        _uiState.value = WeatherUiState.Error("Error loading weather")
    }

    private fun handleResponse(weatherResponse: WeatherResponse, lat: Double, lon: Double) {
        val updatedCity = City(
            name = weatherResponse.city,
            lat = lat,
            lon = lon,
            country = weatherResponse.sys.country,
            state = "--"
        )
        _selectedCity.value = updatedCity
        _uiState.value = WeatherUiState.Success(weatherResponse.toUiModel())
        saveCity(updatedCity)
    }

    private fun WeatherResponse.toUiModel(): WeatherUiModel {
        return WeatherUiModel(
            temp = main.getReadableTemp(),
            description = weather.firstOrNull()?.description ?: "",
            iconUrl = weather.firstOrNull()?.getIconUrl() ?: "",
            feelsLike = main.getFeelsLikeReadableTemp(),
            dateTime = getDateAndTime(),
            visibility = getVisibility(),
            windSpeed = wind.getSpeed(),
            windDirection = wind.getDirection()
        )
    }

    private fun saveCity(city: City) {
        viewModelScope.launch {
            dataStoreManager.saveLastCity(city)
        }
    }

    sealed interface WeatherUiState {
        object Loading : WeatherUiState
        data class Success(val weatherUiModel: WeatherUiModel) : WeatherUiState
        data class Error(val errorMsg: String) : WeatherUiState
    }

    companion object {
        private val DEFAULT_CITY = City("Los Angeles", 34.0549, -118.2426, "US", "California")
    }
}
