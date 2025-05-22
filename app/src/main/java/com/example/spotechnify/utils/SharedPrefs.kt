package com.example.spotechnify.utils

class SharedPrefs @Inject constructor(
    @ApplicationContext context: Context
) {
    private val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun saveAccessToken(token: String) {
        sharedPrefs.edit().putString("access_token", token).apply()
    }

    fun getAccessToken(): String? {
        return sharedPrefs.getString("access_token", null)
    }

    fun clearAuthData() {
        sharedPrefs.edit().remove("access_token").apply()
    }
}