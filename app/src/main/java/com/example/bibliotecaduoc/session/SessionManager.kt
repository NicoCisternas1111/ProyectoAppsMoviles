package com.example.bibliotecaduoc.session

import android.content.Context
import android.content.SharedPreferences
import com.example.bibliotecaduoc.data.network.AuthResponse

object SessionManager {

    private const val PREFS_NAME = "biblioteca_session"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_NAME = "name"
    private const val KEY_EMAIL = "email"
    private const val KEY_ROLE = "role"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        if (!::prefs.isInitialized) {
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    fun saveUser(auth: AuthResponse) {
        prefs.edit()
            .putLong(KEY_USER_ID, auth.id)
            .putString(KEY_NAME, auth.name)
            .putString(KEY_EMAIL, auth.email)
            .putString(KEY_ROLE, auth.role)
            .apply()
    }

    fun logout() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean =
        ::prefs.isInitialized && prefs.contains(KEY_USER_ID)

    fun getUserId(): Long? =
        if (::prefs.isInitialized && prefs.contains(KEY_USER_ID))
            prefs.getLong(KEY_USER_ID, 0L)
        else null

    fun getUserName(): String? =
        if (::prefs.isInitialized) prefs.getString(KEY_NAME, null) else null

    fun getUserEmail(): String? =
        if (::prefs.isInitialized) prefs.getString(KEY_EMAIL, null) else null

    fun getUserRole(): String? =
        if (::prefs.isInitialized) prefs.getString(KEY_ROLE, null) else null
}
