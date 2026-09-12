package com.sundeep1501.weather.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundeep1501.weather.data.WeatherRepository
import com.sundeep1501.weather.data.models.City
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class SearchViewModel @Inject constructor(private val weatherRepository: WeatherRepository) :
    ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val searchUiState: StateFlow<SearchUiState> =
        _searchQuery
            .map { it.trim() }
            .debounce(500.milliseconds)
            .distinctUntilChanged()
            .flatMapLatest { query ->
                flow {
                    if (query.length < 3) {
                        emit(SearchUiState.Empty)
                        return@flow
                    }

                    emit(SearchUiState.Loading)
                    try {
                        emit(SearchUiState.Success(weatherRepository.getCities(query)))
                    } catch (ex: Exception) {
                        if (ex is CancellationException) throw ex
                        emit(SearchUiState.Error("Error:${ex.message ?: "Unknown Error"}"))
                    }
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = SearchUiState.Empty
            )

    fun onSearchQueryChange(newSearchQuery: String) {
        _searchQuery.value = newSearchQuery
    }

    sealed interface SearchUiState {

        object Empty : SearchUiState
        object Loading : SearchUiState
        data class Success(val cities: List<City>) : SearchUiState
        data class Error(val errorMsg: String) : SearchUiState
    }

}