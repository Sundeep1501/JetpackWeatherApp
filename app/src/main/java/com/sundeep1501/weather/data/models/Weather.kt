package com.sundeep1501.weather.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.SimpleTimeZone
import kotlin.math.roundToInt

@Serializable
data class WeatherResponse(
    val weather: List<Weather>,
    val main: WeatherData,
    val visibility: Int,
    val wind: Wind,
    val timezone: Int,
    val dt: Long,
    @SerialName("name") val city: String,
    val sys: Sys
) {
    fun getVisibility(): String {
        val miles = visibility / 1609.344

        // If visibility is high (e.g., 6.2 miles), 1 decimal place is usually preferred.
        // If it is perfect clear visibility (like 10 miles), you can choose to strip decimals.
        return String.format(Locale.US, "%.1f mi", miles)
    }

    fun getDateAndTime(): String {
        val date = Date(dt * 1000)

        // 2. Set up the formatter with your preferred pattern
        val formatter = SimpleDateFormat("EEE, MMM dd 'at' HH:mm:ss", Locale.ENGLISH)

        // 3. Create a raw timezone using the offset milliseconds
        val offsetMilliseconds = timezone * 1000
        val customTimeZone = SimpleTimeZone(offsetMilliseconds, "CustomID")
        formatter.timeZone = customTimeZone

        // 4. Format the time
        return formatter.format(date)
    }
}

@Serializable
data class Sys(val country: String) {

}

@Serializable
data class Wind(val speed: Double, val deg: Int) {

    fun getSpeed(): String {
        val mph = speed * 2.23694
        // Formats the result to one decimal place (e.g., "1.4 mph")
        return String.format(Locale.US, "%.1f mph", mph)
    }

    fun getDirection(): String {

        // Normalize degrees to be within 0 to 359
        val normalizedDeg = ((deg % 360) + 360) % 360

        val directions = arrayOf(
            "N", "NNE", "NE", "ENE",
            "E", "ESE", "SE", "SSE",
            "S", "SSW", "SW", "WSW",
            "W", "WNW", "NW", "NNW"
        )

        // Each sector spans 22.5 degrees (360 / 16).
        // Adding 11.25 offsets the index so 0° (North) centers nicely.
        val index = (((normalizedDeg + 11.25) % 360) / 22.5).toInt()

        return directions[index]
    }
}

@Serializable
data class WeatherData(
    val temp: Double,
    @SerialName("feels_like") val feelsLike: Double,
    val humidity: Int,
) {

    fun getReadableTemp(): String {
        return formatKelvinToFahrenheit(temp, false)
    }


    fun getFeelsLikeReadableTemp(): String {
        return formatKelvinToFahrenheit(feelsLike, false)
    }

}

@Serializable
data class Weather(val id: Int, val main: String, val description: String, val icon: String) {
    fun getIconUrl(): String {
        return "https://openweathermap.org/payload/api/media/file/${icon}.png"
    }
}

/**
 * Converts Kelvin to Fahrenheit formatted string (e.g., "78°F")
 */
fun formatKelvinToFahrenheit(kelvin: Double, showDecimal: Boolean = false): String {
    val fahrenheit = (kelvin - 273.15) * 1.8 + 32
    return if (showDecimal) {
        String.format(Locale.US, "%.1f°F", fahrenheit)
    } else {
        "${fahrenheit.roundToInt()}°F"
    }
}