package com.pnu.ailifelog.model.SignUp

import android.content.Context

object TokenManager {
    private const val PREF_NAME = "user"
    private const val KEY_ACCESS = "accessToken"

    fun saveTokens(context: Context, accessToken: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putString(KEY_ACCESS, accessToken)
            apply()
        }
    }

    fun getAccessToken(context: Context): String? =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_ACCESS, null)

    fun clear(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().clear().apply()
    }
}