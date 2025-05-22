package com.example.spotechnify.Musicdata.Musicmodel

data class Song(
    val id: String,
    val title: String,
    val artistName: String,
    val genre: String,
    val image: String? = null,
    val audioFile: String
)

