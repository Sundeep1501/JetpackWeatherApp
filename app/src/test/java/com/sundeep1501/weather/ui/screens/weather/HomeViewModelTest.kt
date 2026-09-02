package com.sundeep1501.weather.ui.screens.weather

import android.location.Location
import app.cash.turbine.test
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Task
import com.google.common.truth.Truth.assertThat
import com.sundeep1501.weather.data.WeatherRepository
import com.sundeep1501.weather.data.local.DataStoreManager
import com.sundeep1501.weather.data.models.City
import com.sundeep1501.weather.data.models.Sys
import com.sundeep1501.weather.data.models.WeatherResponse
import com.sundeep1501.weather.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: HomeViewModel
    private val repository: WeatherRepository = mockk()
    private val dataStoreManager: DataStoreManager = mockk(relaxed = true)
    private val fusedLocationClient: FusedLocationProviderClient = mockk()

    private val defaultCity = City("Los Angeles", 34.0549, -118.2426, "US", "California")

    @Before
    fun setUp() {
        every { dataStoreManager.getLastCity() } returns flowOf(null)
        viewModel = HomeViewModel(repository, dataStoreManager, fusedLocationClient)
    }

    @Test
    fun `init should load last city from datastore`() = runTest {
        val lastCity = City("London", 51.5074, -0.1278, "GB", "England")
        val weatherResponse = mockk<WeatherResponse>(relaxed = true) {
            every { city } returns "London"
            every { sys } returns Sys("GB")
        }
        every { dataStoreManager.getLastCity() } returns flowOf(lastCity)
        coEvery { repository.getWeather(lastCity.lat, lastCity.lon) } returns weatherResponse

        val vm = HomeViewModel(repository, dataStoreManager, fusedLocationClient)

        // HomeViewModel updates the city with name/country from weather response
        assertThat(vm.selectedCity.value.name).isEqualTo("London")
        assertThat(vm.selectedCity.value.country).isEqualTo("GB")
    }

    @Test
    fun `citySelected should update selectedCity and fetch weather`() = runTest {
        val newCity = City("New York", 40.7128, -74.0060, "US", "New York")
        val weatherResponse = mockk<WeatherResponse>(relaxed = true) {
            every { city } returns "New York"
            every { sys } returns Sys("US")
            every { main.getReadableTemp() } returns "70°F"
        }
        coEvery { repository.getWeather(newCity.lat, newCity.lon) } returns weatherResponse

        viewModel.citySelected(newCity)

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(HomeViewModel.WeatherUiState.Success::class.java)
            assertThat((state as HomeViewModel.WeatherUiState.Success).weatherUiModel.temp).isEqualTo("70°F")
        }
        assertThat(viewModel.selectedCity.value.name).isEqualTo("New York")
        coVerify { dataStoreManager.saveLastCity(any()) }
    }

    @Test
    fun `fetchCurrentLocationWeather should fetch weather for current location`() = runTest {
        val mockLocation = mockk<Location> {
            every { latitude } returns 10.0
            every { longitude } returns 20.0
        }
        val mockTask = mockk<Task<Location>> {
            every { isComplete } returns true
            every { isCanceled } returns false
            every { isSuccessful } returns true
            every { result } returns mockLocation
            every { exception } returns null
        }
        every { fusedLocationClient.getCurrentLocation(any<Int>(), any()) } returns mockTask
        
        val weatherResponse = mockk<WeatherResponse>(relaxed = true) {
            every { city } returns "Test City"
            every { sys } returns Sys("TC")
        }
        coEvery { repository.getWeather(10.0, 20.0) } returns weatherResponse

        viewModel.fetchCurrentLocationWeather()

        assertThat(viewModel.selectedCity.value.lat).isEqualTo(10.0)
        assertThat(viewModel.selectedCity.value.lon).isEqualTo(20.0)
        coVerify { repository.getWeather(10.0, 20.0) }
    }
}
