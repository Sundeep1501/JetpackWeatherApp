package com.sundeep1501.weather.data.models

import kotlinx.serialization.Serializable

@Serializable
data class City(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String,
)
