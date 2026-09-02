package com.sundeep1501.weather.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sundeep1501.weather.data.models.City
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "weather_prefs")

@Singleton
class DataStoreManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json
) {
    private val lastCityKey = stringPreferencesKey("last_city")

    suspend fun saveLastCity(city: City) {
        context.dataStore.edit { preferences ->
            preferences[lastCityKey] = json.encodeToString(city)
        }
    }

    fun getLastCity(): Flow<City?> {
        return context.dataStore.data.map { preferences ->
            preferences[lastCityKey]?.let { jsonString ->
                try {
                    json.decodeFromString<City>(jsonString)
                } catch (e: Exception) {
                    null
                }
            }
        }
    }
}
