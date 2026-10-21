package com.allinonemanager.data

// Logged-in user, kept in memory only: set on login, cleared on logout.
// If Android kills the app, the user has to log in again.
object UserSession {
    var userId: Int? = null

    fun clear() {
        userId = null
    }
}
