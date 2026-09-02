package com.sundeep1501.weather.ui.screens.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundeep1501.weather.data.WeatherRepository
import com.sundeep1501.weather.data.models.City
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class SearchViewModel @Inject constructor(private val weatherRepository: WeatherRepository) :
    ViewModel() {

    private val _searchUiState = MutableStateFlow<SearchUiState>(SearchUiState.Empty)
    val searchUiState = _searchUiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    init {
        viewModelScope.launch {
            _searchQuery.map { it.trim() }.filter { query ->
                if (query.isBlank()) {
                    _searchUiState.value = SearchUiState.Empty
                    false
                } else {
                    query.length >= 3
                }
            }.debounce(500L.milliseconds).distinctUntilChanged().collectLatest { validQuery ->
                search(validQuery)
            }
        }
    }

    private suspend fun search(validQuery: String) {
        try {
            val cities = weatherRepository.getCities(validQuery)
            handleResult(cities)
        } catch (cancelEx: CancellationException) {
            throw cancelEx
        } catch (ex: Exception) {
            handleException(ex)
        }
    }

    fun onSearchQueryChange(newSearchQuery: String) {
        _searchQuery.value = newSearchQuery
    }

    fun handleResult(cities: List<City>) {
        if (cities.isEmpty()) {
            genericError()
        } else {
            _searchUiState.value = SearchUiState.Success(cities)
        }
    }

    fun handleException(ex: Exception) {
        genericError()
        Log.e(SearchViewModel::class.java.name, "onSearchQueryChange, ${ex.message}")
    }

    private fun genericError() {
        _searchUiState.value = SearchUiState.Error("No matching results...")
    }

    sealed interface SearchUiState {

        object Empty : SearchUiState
        object Loading : SearchUiState
        data class Success(val cities: List<City>) : SearchUiState
        data class Error(val errorMsg: String) : SearchUiState
    }

}