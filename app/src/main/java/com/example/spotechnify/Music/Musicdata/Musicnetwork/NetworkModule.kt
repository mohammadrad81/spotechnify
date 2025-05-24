package com.example.spotechnify.Music.Musicdata.Musicnetwork

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor


object NetworkModule {
    private const val BASE_URL = "https://spotechnify.liara.run/"
    private const val TEST_JWT_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbl90eXBlIjoiYWNjZXNzIiwiZXhwIjoxNzUwNTMwODg5LCJpYXQiOjE3NDc5Mzg4ODksImp0aSI6ImI5NzhjNjMzOGVlZTQyYmE5MzFhZThmNGE0NTZkNGJkIiwidXNlcl9pZCI6NCwiZW1haWxfdmVyaWZpZWQiOmZhbHNlfQ.Az6kUOa-zOR7LIvLFk4cHPytZF8W7taugDK8uRvWJXo"

    fun provideMusicService(token: String = TEST_JWT_TOKEN): MusicService {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            if (message.contains("Authorization")) {
                val safeMessage = message.replace(Regex("Bearer\\s+\\S+"), "Bearer ***")
                Log.d("OkHttp", safeMessage)
            } else {
                Log.d("OkHttp", message)
            }
        }
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain: Interceptor.Chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(loggingInterceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(MusicService::class.java)
    }
}

