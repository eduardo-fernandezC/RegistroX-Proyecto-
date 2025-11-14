package com.example.registrox_proyecto.data.remote

import com.example.registrox_proyecto.data.model.ImagenPerfil
import com.example.registrox_proyecto.data.model.Usuario
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface UsuarioApiService {

    @GET("/api/v1/usuarios")
    suspend fun getUsuarios(): Response<List<Usuario>>

    @POST("/api/v1/usuarios")
    suspend fun createUsuario(@Body usuario: Usuario): Response<Usuario>

    @GET("api/v1/imagenes")
    suspend fun getImagenes(): Response<List<ImagenPerfil>>

    @Multipart
    @POST("api/v1/imagenes/upload")
    suspend fun subirImagenMultipart(
        @Part file: MultipartBody.Part,
        @Query("usuarioId") usuarioId: Long
    ): Response<ImagenPerfil>
}
