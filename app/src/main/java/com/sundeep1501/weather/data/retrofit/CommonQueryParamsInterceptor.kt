package com.sundeep1501.weather.data.retrofit

import com.sundeep1501.weather.di.NetworkModule
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommonQueryParamsInterceptor @Inject constructor(@NetworkModule.CommonQueryParams private val map: Map<String, String>) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newUrl = request.url.newBuilder().apply {
            for ((name, value) in map) {
                setQueryParameter(name, value)
            }
        }.build()
        return chain.proceed(request.newBuilder().url(newUrl).build())
    }
}