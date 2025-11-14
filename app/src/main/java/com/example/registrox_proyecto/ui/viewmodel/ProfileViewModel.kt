package com.example.registrox_proyecto.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.registrox_proyecto.data.datastore.ProfileDataStore
import com.example.registrox_proyecto.data.remote.RetrofitInstance
import com.example.registrox_proyecto.data.repository.AuthRepository
import com.example.registrox_proyecto.utils.NetworkUtils
import com.example.registrox_proyecto.utils.uriToMultipart
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStore = ProfileDataStore(application)
    private val authRepository = AuthRepository()

    val imageUriState = mutableStateOf<String?>(null)

    init {
        viewModelScope.launch {
            dataStore.imageUri.collectLatest { uri ->
                if (!uri.isNullOrBlank()) {
                    imageUriState.value = uri
                }
            }
        }
    }

    fun clearImage() {
        viewModelScope.launch {
            dataStore.clearImageUri()
            imageUriState.value = null
        }
    }

    fun subirImagen(usuarioId: Long, localUri: Uri) {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            if (!NetworkUtils.isNetworkAvailable(context)) return@launch

            try {
                val multipart = uriToMultipart(context, localUri, "file")

                val response = RetrofitInstance.api.subirImagenMultipart(
                    multipart,
                    usuarioId
                )

                if (response.isSuccessful) {
                    val imageUrl = response.body()?.imageUrl ?: ""
                    dataStore.saveImageUri(imageUrl)
                    imageUriState.value = imageUrl
                }

            } catch (_: Exception) {
            }
        }
    }

    fun sincronizarImagenDesdeApi(usuarioId: Long) {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            if (!NetworkUtils.isNetworkAvailable(context)) return@launch

            try {
                val url = authRepository.getImagenPorUsuarioId(usuarioId)
                if (url != null) {
                    dataStore.saveImageUri(url)
                    imageUriState.value = url
                }
            } catch (_: Exception) {
            }
        }
    }
}
