package com.example.josephwanis.reportingsystem.data.session

import android.content.Context
import com.example.josephwanis.reportingsystem.data.models.User

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveUserSession(user: User) {
        val editor = prefs.edit()
        editor.putString("userId", user.userId)
        editor.putString("displayName", user.displayName)
        editor.putString("email", user.email)
        editor.apply()
    }

    fun getUserSession(): User? {
        val userId = prefs.getString("userId", null)
        val displayName = prefs.getString("displayName", null)
        val email = prefs.getString("email", null)

        return if (userId != null && displayName != null && email != null) {
            User(userId, displayName, email)
        } else {
            null
        }
    }

    fun clearUserSession() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}
