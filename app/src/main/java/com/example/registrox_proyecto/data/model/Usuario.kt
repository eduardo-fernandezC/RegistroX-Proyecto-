package com.example.registrox_proyecto.data.model

data class Rol(
    val id: Long? = null,
    val nombre: String? = null
)

data class Usuario(
    val id: Long? = null,
    val email: String,
    val password: String? = null,
    val rol: Rol
)