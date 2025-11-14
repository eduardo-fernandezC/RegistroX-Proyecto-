package com.example.registrox_proyecto.ui.viewmodel

import android.util.Log
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

    private val _mensaje = MutableStateFlow<String>("")
    val mensaje: StateFlow<String> = _mensaje

    fun cargarEntradas() {
        viewModelScope.launch {
            val response = repository.obtenerEntradas()
            if (response.isSuccessful) {
                val lista = response.body() ?: emptyList()
                lista.forEach { entrada ->
                    Log.d("ENTRADA_API", "Título: ${entrada.titulo} | Email: ${entrada.usuarioEmail}")
                }
                _entradas.value = lista
            } else {
                _mensaje.value = "Error: ${response.code()}"
            }
        }
    }

}
