package com.example.registrox_proyecto.data.remote

import com.example.registrox_proyecto.data.model.Compra
import retrofit2.Response
import retrofit2.http.*

interface ComprasApiService {

    @GET("api/v1/compras")
    suspend fun getCompras(): Response<List<Compra>>

    @POST("api/v1/compras")
    suspend fun createCompra(@Body compra: Compra): Response<Compra>
}
