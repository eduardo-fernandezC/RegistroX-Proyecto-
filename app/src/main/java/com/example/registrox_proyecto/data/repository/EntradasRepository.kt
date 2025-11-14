package com.example.registrox_proyecto.data.repository

import com.example.registrox_proyecto.data.remote.RetrofitInstance
import retrofit2.Response

class EntradasRepository {
    private val api = RetrofitInstance.apiEntradas

    suspend fun obtenerEntradas() = api.getEntradas()

    suspend fun marcarEntradaUsada(codigoQR: String): Response<Map<String, String>> {
        return RetrofitInstance.apiEntradas.marcarEntradaUsada(codigoQR)
    }

}
