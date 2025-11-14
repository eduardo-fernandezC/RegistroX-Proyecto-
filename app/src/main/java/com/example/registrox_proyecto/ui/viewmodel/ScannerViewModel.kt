package com.example.registrox_proyecto.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registrox_proyecto.data.repository.EntradasRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State

class ScannerViewModel(
    private val entradasRepository: EntradasRepository = EntradasRepository()
) : ViewModel() {

    private val _mensajeOperacion = mutableStateOf("")
    val mensajeOperacion: State<String> = _mensajeOperacion

    fun validarEntrada(codigoQR: String) {
        viewModelScope.launch {
            try {
                val response = entradasRepository.marcarEntradaUsada(codigoQR)
                if (response.isSuccessful) {
                    _mensajeOperacion.value = response.body()?.get("mensaje") ?: "Entrada validada correctamente"
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                    _mensajeOperacion.value = errorMsg
                }
            } catch (e: Exception) {
                _mensajeOperacion.value = "Error de conexión: ${e.localizedMessage}"
            }
        }
    }
}
