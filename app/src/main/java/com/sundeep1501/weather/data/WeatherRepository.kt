package com.sundeep1501.weather.data

import com.sundeep1501.weather.data.models.City
import com.sundeep1501.weather.data.models.WeatherResponse
import com.sundeep1501.weather.data.retrofit.OpenWeatherApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(private val openWeatherApi: OpenWeatherApi) {

    suspend fun getCities(searchQuery: String): List<City> {
        return openWeatherApi.getCitiesByName(searchQuery)
    }

    suspend fun getWeather(lat: Double, lon: Double): WeatherResponse{
        return openWeatherApi.getWeather(lat, lon)
    }


}