package com.example.registrox_proyecto.data.repository

import com.example.registrox_proyecto.data.remote.RetrofitInstance
import com.example.registrox_proyecto.data.model.Usuario
import retrofit2.Response

class UsuarioRepository {

    suspend fun obtenerUsuarios(): Response<List<Usuario>> =
        RetrofitInstance.api.getUsuarios()

    suspend fun crearUsuario(usuario: Usuario): Response<Usuario> =
        RetrofitInstance.api.createUsuario(usuario)

}
