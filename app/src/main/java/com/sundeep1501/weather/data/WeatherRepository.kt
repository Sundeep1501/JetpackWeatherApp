package com.sundeep1501.weather.data

import com.sundeep1501.weather.data.models.City
import com.sundeep1501.weather.data.models.WeatherResponse
import com.sundeep1501.weather.data.retrofit.OpenWeatherApi
import com.sundeep1501.weather.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val openWeatherApi: OpenWeatherApi,
    @IODispatcher private val dispatcher: CoroutineDispatcher
) {

    suspend fun getCities(searchQuery: String): List<City> = withContext(dispatcher) {
        return@withContext openWeatherApi.getCitiesByName(searchQuery)
    }

    suspend fun getWeather(lat: Double, lon: Double): WeatherResponse = withContext(dispatcher) {
        return@withContext openWeatherApi.getWeather(lat, lon)
    }

}