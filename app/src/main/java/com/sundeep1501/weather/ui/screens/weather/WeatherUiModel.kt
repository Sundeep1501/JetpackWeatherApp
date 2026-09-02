package com.sundeep1501.weather.ui.screens.weather

data class WeatherUiModel(
    val temp: String,
    val description: String,
    val iconUrl: String,
    val feelsLike: String,
    val dateTime: String,
    val visibility: String,
    val windSpeed: String,
    val windDirection: String
)
