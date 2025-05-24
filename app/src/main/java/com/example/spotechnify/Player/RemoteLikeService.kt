package com.example.spotechnify.Player
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.converter.scalars.ScalarsConverterFactory


class RemoteLikeService (token: String): LikeRepository{
    private val api = LikeService.provideLikeService(token)
    override suspend fun likeTrack(id: Int): LikeServiceResult {
        try {
            val resp = api.likeSong(id)
            if (resp.isSuccessful){
                return LikeServiceResult.Success
            } else {
                return LikeServiceResult.Error(resp.code(), resp.errorBody()?.string())
            }
        } catch (e: Throwable){
            return LikeServiceResult.Exception(e)
        }
    }
    override suspend fun unlikeTrack(id: Int): LikeServiceResult {
        try {
            val resp = api.unlike(id)
            if (resp.isSuccessful){
                return LikeServiceResult.Success
            } else {
                return LikeServiceResult.Error(resp.code(), resp.errorBody()?.string())
            }
        } catch (e: Throwable){
            return LikeServiceResult.Exception(e)
        }
    }
}

interface RemoteLikeAPIService {
    @POST("music/like/{Id}/")
    suspend fun likeSong(@Path("Id") trackId: Int): Response<String>

    @DELETE("music/unlike/{Id}/")
    suspend fun unlike(@Path("Id") trackId: Int): Response<String>
}

object LikeService {
    private const val BASE_URL = "https://spotechnify.liara.run/"

    fun provideLikeService(token: String): RemoteLikeAPIService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor { chain: Interceptor.Chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        return retrofit.create(RemoteLikeAPIService::class.java)
    }
}