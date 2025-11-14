package com.example.registrox_proyecto.data.repository

import com.example.registrox_proyecto.data.remote.RetrofitInstance

class ComprasRepository {
    private val api = RetrofitInstance.apiCompras

    suspend fun obtenerCompras() = api.getCompras()
}
