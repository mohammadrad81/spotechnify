package com.example.spotechnify.Musicdata.Musicmodel

import com.google.gson.annotations.SerializedName

data class Song(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("artist_name") val artistName: String,
    @SerializedName("genre") val genre: String,
    @SerializedName("image") val image: String? = null,
    @SerializedName("liked") val liked: Boolean? = false,
    @SerializedName("audio_file") val audioFile: String? = null
)

