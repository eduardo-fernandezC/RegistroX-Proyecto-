package com.example.registrox_proyecto.data.repository

import com.example.registrox_proyecto.data.model.Entrada
import com.example.registrox_proyecto.data.model.entradasDisponibles

class EntradasRepository {

    fun obtenerTodas(): List<Entrada> = entradasDisponibles

    fun buscarPorTitulo(query: String): List<Entrada> {
        if (query.isBlank()) return entradasDisponibles
        return entradasDisponibles.filter {
            it.titulo.contains(query, ignoreCase = true)
        }
    }
}