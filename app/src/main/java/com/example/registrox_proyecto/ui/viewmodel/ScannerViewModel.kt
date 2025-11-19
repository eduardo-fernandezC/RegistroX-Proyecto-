package com.example.registrox_proyecto.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registrox_proyecto.data.repository.EntradasRepository
import com.example.registrox_proyecto.utils.NetworkUtils
import kotlinx.coroutines.launch

class ScannerViewModel(
    private val entradasRepository: EntradasRepository = EntradasRepository()
) : ViewModel() {

    private val _mensajeOperacion = mutableStateOf("")
    val mensajeOperacion: State<String> = _mensajeOperacion

    fun validarEntrada(codigoQR: String, hasInternet: Boolean) {
        viewModelScope.launch {

            // 🔥 No usamos context aquí, recibimos el estado desde la UI
            if (!hasInternet) {
                _mensajeOperacion.value = "Sin conexión a internet"
                return@launch
            }

            try {
                val response = entradasRepository.marcarEntradaUsada(codigoQR)
                if (response.isSuccessful) {
                    _mensajeOperacion.value =
                        response.body()?.get("mensaje") ?: "Entrada validada correctamente"
                } else {
                    _mensajeOperacion.value =
                        response.errorBody()?.string() ?: "Error desconocido"
                }
            } catch (e: Exception) {
                _mensajeOperacion.value = "Error de conexión"
            }
        }
    }
}
