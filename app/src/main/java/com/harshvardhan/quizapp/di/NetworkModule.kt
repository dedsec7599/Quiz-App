package com.harshvardhan.quizapp.di

import com.harshvardhan.quizapp.apiService.ApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

val networkModule = module {
    single { RetrofitConfig.retrofit }
}

object RetrofitConfig {
    const val BASE_URL = "https://www.google.com/"
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    val contentType = "application/json".toMediaType()

    private val client = OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS).writeTimeout(5, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder().build()
            chain.proceed(request)
        }.build()

    @OptIn(ExperimentalSerializationApi::class)
    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL).client(client)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()
        .create(ApiService::class.java)
}