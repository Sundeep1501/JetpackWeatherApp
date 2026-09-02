package com.sundeep1501.weather.data

import com.google.common.truth.Truth.assertThat
import com.sundeep1501.weather.data.models.City
import com.sundeep1501.weather.data.models.WeatherResponse
import com.sundeep1501.weather.data.retrofit.OpenWeatherApi
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class WeatherRepositoryTest {

    private lateinit var repository: WeatherRepository
    private val api: OpenWeatherApi = mockk()

    @Before
    fun setUp() {
        repository = WeatherRepository(api)
    }

    @Test
    fun `getCities should return list of cities from api`() = runTest {
        // Given
        val cities = listOf(City("London", 51.5074, -0.1278, "GB", "England"))
        coEvery { api.getCitiesByName("London") } returns cities

        // When
        val result = repository.getCities("London")

        // Then
        assertThat(result).isEqualTo(cities)
    }

    @Test
    fun `getWeather should return weather response from api`() = runTest {
        // Given
        val weatherResponse = mockk<WeatherResponse>()
        coEvery { api.getWeather(51.5074, -0.1278) } returns weatherResponse

        // When
        val result = repository.getWeather(51.5074, -0.1278)

        // Then
        assertThat(result).isEqualTo(weatherResponse)
    }
}
