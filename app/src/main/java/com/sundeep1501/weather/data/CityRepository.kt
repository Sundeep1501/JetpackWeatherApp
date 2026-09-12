package com.sundeep1501.weather.data

import com.sundeep1501.weather.data.models.City
import com.sundeep1501.weather.data.retrofit.OpenWeatherApi
import com.sundeep1501.weather.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CityRepository @Inject constructor(
    private val weatherApi: OpenWeatherApi,
    @IODispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend fun getCities(query: String): List<City> = withContext(dispatcher) {
        return@withContext weatherApi.getCitiesByName(query)
    }

}