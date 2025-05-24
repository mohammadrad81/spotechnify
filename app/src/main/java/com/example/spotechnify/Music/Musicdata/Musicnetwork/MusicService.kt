package com.example.spotechnify.Music.Musicdata.Musicnetwork

import com.example.spotechnify.Music.Musicdata.Musicmodel.Song
import retrofit2.http.GET
import retrofit2.http.Query

interface MusicService {
    @GET("music/list/")
    suspend fun getAllSongs(): List<Song>

    @GET("music/recommend/")
    suspend fun getRecommendedSongs(): List<Song>

    @GET("music/liked/")
    suspend fun getLikedSongs(): List<Song>

    @GET("music/search/")
    suspend fun searchSongs(@Query("q") query: String): List<Song>
}

