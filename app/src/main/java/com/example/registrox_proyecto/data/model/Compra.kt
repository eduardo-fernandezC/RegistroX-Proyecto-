package com.example.registrox_proyecto.data.model

data class Compra(
    val id: Long? = null,
    val fechaCompra: String? = null,
    val usuario: Usuario,
    val compraEntradas: List<CompraEntrada>? = emptyList()
)
