package com.example.registrox_proyecto.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.authDataStore by preferencesDataStore("auth_prefs")

class AuthDataStore(private val context: Context) {

    companion object {
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val ROLE_KEY = stringPreferencesKey("role")
    }

    val email: Flow<String?> = context.authDataStore.data.map { prefs ->
        prefs[EMAIL_KEY] ?: ""
    }

    val role: Flow<String?> = context.authDataStore.data.map { prefs ->
        prefs[ROLE_KEY] ?: ""
    }

    suspend fun saveUser(email: String, role: String) {
        context.authDataStore.edit { prefs ->
            prefs[EMAIL_KEY] = email
            prefs[ROLE_KEY] = role
        }
    }

    suspend fun clearUser() {
        context.authDataStore.edit { prefs ->
            prefs.remove(EMAIL_KEY)
            prefs.remove(ROLE_KEY)
        }
    }

}
