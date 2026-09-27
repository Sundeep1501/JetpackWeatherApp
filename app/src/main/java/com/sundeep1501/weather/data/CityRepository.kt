package com.sundeep1501.weather.data

import com.sundeep1501.weather.data.local.DataStoreManager
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
    private val dataStoreManager: DataStoreManager,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun getCities(query: String): List<City> = withContext(ioDispatcher) {
        weatherApi.getCitiesByName(query)
    }

    suspend fun getLastCity(): City? = withContext(ioDispatcher) { dataStoreManager.getLastCity() }

    suspend fun saveLastCity(city: City) = withContext(ioDispatcher) {
        dataStoreManager.saveLastCity(city)
    }
}