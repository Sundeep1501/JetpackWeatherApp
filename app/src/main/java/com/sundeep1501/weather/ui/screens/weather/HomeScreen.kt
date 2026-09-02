package com.sundeep1501.weather.ui.screens.weather

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    searchClicked: () -> Unit
) {
    val city by viewModel.selectedCity.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()

        ) {
            Text(text = "Weather App", modifier = Modifier.align(Alignment.CenterVertically))
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = searchClicked) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search action"
                )
            }
        }
        Text(
            text = city.name,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = city.country,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        when (val state = uiState) {
            is HomeViewModel.WeatherUiState.Error -> {
                Text(text = state.errorMsg)
            }

            HomeViewModel.WeatherUiState.Loading -> {
                Text(text = "Loading...")
            }

            is HomeViewModel.WeatherUiState.Success -> {
                val weatherUiModel = state.weatherUiModel
                Text(
                    text = weatherUiModel.temp,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp)
                )
                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .align(Alignment.CenterHorizontally)
                ) {
                    AsyncImage(model = weatherUiModel.iconUrl, contentDescription = "")
                    Text(
                        text = weatherUiModel.description,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                WeatherRow("Feels Like", weatherUiModel.feelsLike)
                WeatherRow("Date & Time", weatherUiModel.dateTime)
                WeatherRow("Visibility", weatherUiModel.visibility)
                WeatherRow(
                    "Wind",
                    "${weatherUiModel.windSpeed}, ${weatherUiModel.windDirection}"
                )
            }
        }
    }
}

@Composable
fun WeatherRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .wrapContentSize()
    ) {
        Text(text = "$label: ")
        Text(
            text = value,
            fontWeight = FontWeight.Bold
        )
    }
}


