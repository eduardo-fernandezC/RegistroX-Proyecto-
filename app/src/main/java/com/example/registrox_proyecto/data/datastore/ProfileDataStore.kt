package com.example.registrox_proyecto.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.profileDataStore by preferencesDataStore("profile_prefs")

class ProfileDataStore(private val context: Context) {

    private val IMAGE_KEY = stringPreferencesKey("profile_image_uri")

    val imageUri: Flow<String?> = context.profileDataStore.data.map { prefs ->
        prefs[IMAGE_KEY]
    }

    suspend fun saveImageUri(uri: String) {
        context.profileDataStore.edit { prefs ->
            prefs[IMAGE_KEY] = uri
        }
    }

    suspend fun clearImageUri() {
        context.profileDataStore.edit { prefs ->
            prefs.remove(IMAGE_KEY)
        }
    }
}
