package com.example.registrox_proyecto.data.model

data class Entrada(
    val id: String,
    val titulo: String,
    val lugar: String,
    val precio: Double,
    val estado: String = "disponible",
    val codigoQR: String? = null,
    val cantidad: Int = 1,
    val usuarioEmail: String = ""
)

