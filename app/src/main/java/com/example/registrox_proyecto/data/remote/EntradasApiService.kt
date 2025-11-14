package com.example.registrox_proyecto.data.remote

import com.example.registrox_proyecto.data.model.Entrada
import retrofit2.Response
import retrofit2.http.*

interface EntradasApiService {

    @GET("api/v1/entradas")
    suspend fun getEntradas(): Response<List<Entrada>>

    @PATCH("api/v1/entradas/usar/{codigoQR}")
    suspend fun marcarEntradaUsada(@Path("codigoQR") codigoQR: String): Response<Map<String, String>>
}
