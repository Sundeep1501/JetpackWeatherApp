package com.sundeep1501.weather.ui.screens.search

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.sundeep1501.weather.data.WeatherRepository
import com.sundeep1501.weather.data.models.City
import com.sundeep1501.weather.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: SearchViewModel
    private val repository: WeatherRepository = mockk()

    @Before
    fun setUp() {
        viewModel = SearchViewModel(repository)
    }

    @Test
    fun `initial state should be Empty`() = runTest {
        assertThat(viewModel.searchUiState.value).isEqualTo(SearchViewModel.SearchUiState.Empty)
    }

    @Test
    fun `onSearchQueryChange should update searchQuery`() = runTest {
        viewModel.onSearchQueryChange("Lon")
        assertThat(viewModel.searchQuery.value).isEqualTo("Lon")
    }

    @Test
    fun `short query should not trigger search and keep state Empty`() = runTest {
        viewModel.onSearchQueryChange("Lo")
        advanceTimeBy(600) // Debounce is 500ms
        assertThat(viewModel.searchUiState.value).isEqualTo(SearchViewModel.SearchUiState.Empty)
    }

    @Test
    fun `valid query should trigger search and update state to Success`() = runTest {
        val cities = listOf(City("London", 51.5074, -0.1278, "GB", "England"))
        coEvery { repository.getCities("London") } returns cities

        viewModel.onSearchQueryChange("London")
        
        viewModel.searchUiState.test {
            // Initial Empty state (or previous state)
            assertThat(awaitItem()).isEqualTo(SearchViewModel.SearchUiState.Empty)
            
            // Advance time for debounce
            advanceTimeBy(600)
            
            val state = awaitItem()
            assertThat(state).isInstanceOf(SearchViewModel.SearchUiState.Success::class.java)
            assertThat((state as SearchViewModel.SearchUiState.Success).cities).isEqualTo(cities)
        }
    }

    @Test
    fun `empty query should reset state to Empty`() = runTest {
        viewModel.onSearchQueryChange("London")
        advanceTimeBy(600)
        
        viewModel.onSearchQueryChange("")
        assertThat(viewModel.searchUiState.value).isEqualTo(SearchViewModel.SearchUiState.Empty)
    }

    @Test
    fun `search with no results should update state to Error`() = runTest {
        coEvery { repository.getCities("UnknownCity") } returns emptyList()

        viewModel.onSearchQueryChange("UnknownCity")
        
        viewModel.searchUiState.test {
            assertThat(awaitItem()).isEqualTo(SearchViewModel.SearchUiState.Empty)
            advanceTimeBy(600)
            val state = awaitItem()
            assertThat(state).isInstanceOf(SearchViewModel.SearchUiState.Error::class.java)
        }
    }
}
