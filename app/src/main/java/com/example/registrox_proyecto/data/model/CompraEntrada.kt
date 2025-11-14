package com.example.registrox_proyecto.data.model

data class CompraEntrada(
    val id: Long? = null,
    val estado: String = "disponible",
    val codigoQR: String? = null,
    val entrada: Entrada? = null
)
