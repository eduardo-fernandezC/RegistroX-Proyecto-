package com.example.registrox_proyecto.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.registrox_proyecto.data.datastore.ProfileDataStore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStore = ProfileDataStore(application)
    val imageUriState = mutableStateOf<String?>(null)

    init {
        viewModelScope.launch {
            dataStore.imageUri.collectLatest { uri ->
                if (uri != null && uri.isNotBlank()) {
                    imageUriState.value = uri
                }
            }
        }
    }

    fun saveImage(uri: String) {
        viewModelScope.launch {
            dataStore.saveImageUri(uri)
            imageUriState.value = uri
        }
    }

    fun clearImage() {
        viewModelScope.launch {
            dataStore.clearImageUri()
            imageUriState.value = null
        }
    }
}
