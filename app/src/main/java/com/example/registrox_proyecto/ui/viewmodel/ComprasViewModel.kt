package com.example.registrox_proyecto.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registrox_proyecto.data.model.Compra
import com.example.registrox_proyecto.data.repository.ComprasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ComprasViewModel(
    private val repository: ComprasRepository = ComprasRepository()
) : ViewModel() {

    private val _compras = MutableStateFlow<List<Compra>>(emptyList())
    val compras: StateFlow<List<Compra>> = _compras

    private val _mensaje = MutableStateFlow<String>("")
    val mensaje: StateFlow<String> = _mensaje

    fun cargarCompras() {
        viewModelScope.launch {
            try {
                val response = repository.obtenerCompras()
                if (response.isSuccessful) {
                    _compras.value = response.body() ?: emptyList()
                    _mensaje.value = ""
                } else {
                    _mensaje.value = "Error al obtener compras: ${response.code()}"
                }
            } catch (e: Exception) {
                _mensaje.value = "Sin conexión a internet"
            }
        }
    }


}
