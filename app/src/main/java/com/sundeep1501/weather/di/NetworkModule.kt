package com.sundeep1501.weather.di

import com.sundeep1501.weather.data.retrofit.OpenWeatherApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun providesJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    fun providesOkHttp(): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }).build()
    }

    @Provides
    fun providesRetrofit(json: Json, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .baseUrl(OpenWeatherApi.BASE_URL)
            .client(okHttpClient)
            .build()
    }

    @Provides
    fun providesWeatherApi(retrofit: Retrofit): OpenWeatherApi {
        return retrofit.create(OpenWeatherApi::class.java)
    }

}