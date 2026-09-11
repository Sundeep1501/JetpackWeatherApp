package com.sundeep1501.weather.di

import com.sundeep1501.weather.data.retrofit.CommonQueryParamsInterceptor
import com.sundeep1501.weather.data.retrofit.OpenWeatherApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    @Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
    annotation class CommonQueryParams

    @Provides
    @Singleton
    fun providesJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    @CommonQueryParams
    fun providesCommonQueryParams(): Map<String, String> {
        return mapOf(OpenWeatherApi.QUERY_PARAM_APP_ID to OpenWeatherApi.API_KEY)
    }

    @Provides
    @Singleton
    fun providesOkHttp(apiKeyInterceptor: CommonQueryParamsInterceptor): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(apiKeyInterceptor).build()
    }

    @Provides
    @Singleton
    fun providesRetrofit(json: Json, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .baseUrl(OpenWeatherApi.BASE_URL)
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun providesWeatherApi(retrofit: Retrofit): OpenWeatherApi {
        return retrofit.create(OpenWeatherApi::class.java)
    }

}