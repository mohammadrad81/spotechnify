package com.example.spotechnify.Music.Musicdata.Musicmodel

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Song(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("artist_name") val artistName: String,
    @SerializedName("genre") val genre: String,
    @SerializedName("image") val image: String,
    @SerializedName("liked") val liked: Boolean,
    @SerializedName("audio_file") val audioFile: String
)

