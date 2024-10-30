package com.dicoding.asclepius.data.remote.retrofit

import android.util.Log
import com.dicoding.asclepius.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

object ApiConfig {
    private const val BASE_URL = BuildConfig.BASE_URL

    fun getApiService(): ApiService {
        val apiKeyInterceptor = Interceptor { chain ->
            val request = chain.request()
            val requestHeaders = request.newBuilder()
            if (BuildConfig.API_KEY.isNotBlank()) {
                requestHeaders.addHeader("Authorization", "Bearer ${BuildConfig.API_KEY}")
            } else {
                Log.e("ApiConfig", "API Key is missing!")
            }
            chain.proceed(requestHeaders.build())
        }

        val loggingInterceptor = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(RetryInterceptor()) // Custom retry interceptor for failed requests
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ApiService::class.java)
    }
    private class RetryInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            var response: Response? = null
            var tryCount = 0
            val maxLimit = 3

            while (response == null && tryCount < maxLimit) {
                try {
                    response = chain.proceed(chain.request())
                } catch (e: IOException) {
                    Log.w("RetryInterceptor", "Request failed - retrying (${tryCount + 1})...")
                    tryCount++
                }
            }
            return response ?: throw IOException("Failed to connect after $maxLimit attempts")
        }
    }
}