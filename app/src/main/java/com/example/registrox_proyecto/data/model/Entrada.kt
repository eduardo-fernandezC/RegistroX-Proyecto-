package com.example.registrox_proyecto.data.model

data class Entrada(
    val id: Long? = null,
    val titulo: String,
    val lugar: String,
    val precio: Double,
    val estado: String,
    val codigoQR: String? = null,
    val cantidad: Int = 1,
    val usuarioEmail: String? = null
)


