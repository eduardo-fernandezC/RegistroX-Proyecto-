package com.example.registrox_proyecto.data.repository

import android.util.Log
import com.example.registrox_proyecto.data.remote.RetrofitInstance
import com.example.registrox_proyecto.data.model.Rol
import com.example.registrox_proyecto.data.model.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository {

    suspend fun register(email: String, password: String): Boolean {
        return try {
            val rol = if (email.endsWith("@registrox.cl"))
                Rol(id = 1, nombre = "TRABAJADOR")
            else
                Rol(id = 2, nombre = "USUARIO")

            val nuevoUsuario = Usuario(
                email = email,
                password = password,
                rol = rol
            )

            val response = withContext(Dispatchers.IO) {
                RetrofitInstance.api.createUsuario(nuevoUsuario)
            }

            if (response.isSuccessful) {
                Log.d("API_REGISTER", "Usuario creado: ${response.body()}")
                true
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("API_REGISTER", "Error HTTP ${response.code()} ${response.message()}")
                Log.e("API_DEBUG", "Cuerpo del error: $errorBody")
                false
            }

        } catch (e: Exception) {
            Log.e("API_REGISTER", "Error: ${e.localizedMessage}")
            false
        }
    }


    suspend fun login(email: String, password: String): Usuario? {
        return try {
            val response = withContext(Dispatchers.IO) {
                RetrofitInstance.api.getUsuarios()
            }

            if (response.isSuccessful) {
                val usuarios = response.body() ?: emptyList()
                val usuario = usuarios.find { it.email.equals(email, ignoreCase = true) }

                if (usuario != null) {
                    Log.d("API_LOGIN", "Usuario encontrado: ${usuario.email} (Rol: ${usuario.rol.nombre})")
                    usuario
                } else {
                    Log.e("API_LOGIN", "Usuario no encontrado con email: $email")
                    null
                }

            } else {
                Log.e("API_LOGIN", "Error HTTP ${response.code()} ${response.message()}")
                null
            }

        } catch (e: Exception) {
            Log.e("API_LOGIN", "Error de red: ${e.localizedMessage}")
            null
        }
    }

    suspend fun getImagenPorUsuarioId(usuarioId: Long): String? {
        return try {
            val response = withContext(Dispatchers.IO) {
                RetrofitInstance.api.getImagenes()
            }

            if (response.isSuccessful) {
                val imagenes = response.body() ?: emptyList()
                val imagenUsuario = imagenes.find { it.usuario.id == usuarioId }
                imagenUsuario?.imageUrl
            } else {
                Log.e("API_IMAGEN", "Error al obtener imágenes: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("API_IMAGEN", "Error al recuperar imagen: ${e.localizedMessage}")
            null
        }
    }

    fun logout(): Boolean = true
}
