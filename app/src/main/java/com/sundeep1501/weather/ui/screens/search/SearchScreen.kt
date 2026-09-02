package com.sundeep1501.weather.ui.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sundeep1501.weather.data.models.City

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
    onCitySelected: (city: City) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val uiState by viewModel.searchUiState.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "Enter city name") },
            value = searchQuery,
            onValueChange = { newValue -> viewModel.onSearchQueryChange(newValue) })

        when (val state = uiState) {
            SearchViewModel.SearchUiState.Empty -> {

            }

            is SearchViewModel.SearchUiState.Error -> {
                Text(
                    modifier = modifier.align(Alignment.CenterHorizontally),
                    text = state.errorMsg
                )
            }

            SearchViewModel.SearchUiState.Loading -> {
                CircularProgressIndicator(modifier = modifier.align(Alignment.CenterHorizontally))
            }

            is SearchViewModel.SearchUiState.Success -> {
                LazyColumn(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(
                        items = state.cities,
                        key = { city -> "${city.lat}+${city.lat}" }) { city ->
                        CityRow(
                            city = city,
                            onClick = onCitySelected
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CityRow(modifier: Modifier = Modifier, city: City, onClick: (city: City) -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = {
                onClick(city)
            })
    ) {
        Text(
            text = city.name,
            modifier = modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "${city.state}, ${city.country}",
            modifier = modifier.padding(bottom = 8.dp),
            style = MaterialTheme.typography.bodySmall
        )
        HorizontalDivider()
    }
}