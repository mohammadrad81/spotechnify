package com.example.spotechnify

interface LikeRepository {
    suspend fun likeTrack(id: Int): LikeServiceResult
    suspend fun unlikeTrack(id: Int): LikeServiceResult
}

sealed class LikeServiceResult {
    data object Success : LikeServiceResult()
    data class Error(val code: Int, val message: String?) : LikeServiceResult()
    data class Exception(val exception: Throwable) : LikeServiceResult()
}