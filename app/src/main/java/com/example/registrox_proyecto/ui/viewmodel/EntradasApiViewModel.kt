package com.example.registrox_proyecto.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registrox_proyecto.data.model.Entrada
import com.example.registrox_proyecto.data.repository.EntradasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EntradasApiViewModel(
    private val repository: EntradasRepository = EntradasRepository()
) : ViewModel() {

    private val _entradas = MutableStateFlow<List<Entrada>>(emptyList())
    val entradas: StateFlow<List<Entrada>> = _entradas

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje

    fun cargarEntradas(hasInternet: Boolean) {
        viewModelScope.launch {

            if (!hasInternet) {
                _mensaje.value = "Sin conexion a internet"
                return@launch
            }

            try {
                val response = repository.obtenerEntradas()
                if (response.isSuccessful) {
                    _entradas.value = response.body() ?: emptyList()
                } else {
                    _mensaje.value = "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                _mensaje.value = "Error de red"
            }
        }
    }
}
