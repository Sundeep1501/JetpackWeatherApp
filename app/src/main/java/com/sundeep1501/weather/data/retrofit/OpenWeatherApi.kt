package com.sundeep1501.weather.data.retrofit

import com.sundeep1501.weather.data.models.City
import com.sundeep1501.weather.data.models.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherApi {
    @GET("geo/1.0/direct")
    suspend fun getCitiesByName(
        @Query("q") searchTerm: String,
        @Query("limit") limit: Int = 5
    ): List<City>

    @GET("data/2.5/weather")
    suspend fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double
    ): WeatherResponse

    companion object {
        const val BASE_URL = "https://api.openweathermap.org"
        const val QUERY_PARAM_APP_ID = "appid"
    }
}