package com.example.registrox_proyecto.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registrox_proyecto.data.model.Usuario
import com.example.registrox_proyecto.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UsuarioViewModel(
    private val repository: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    private val _usuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val usuarios: StateFlow<List<Usuario>> = _usuarios

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje

    fun cargarUsuarios() {
        viewModelScope.launch {
            val response = repository.obtenerUsuarios()
            if (response.isSuccessful) {
                _usuarios.value = response.body() ?: emptyList()
            } else {
                _mensaje.value = "Error al cargar usuarios: ${response.code()}"
            }
        }
    }

    fun crearUsuario(usuario: Usuario) {
        viewModelScope.launch {
            val response = repository.crearUsuario(usuario)
            if (response.isSuccessful) {
                _mensaje.value = "Usuario creado correctamente"
                cargarUsuarios()
            } else {
                _mensaje.value = "Error al crear usuario"
            }
        }
    }

}
