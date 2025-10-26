package com.example.registrox_proyecto.data.repository

import com.example.registrox_proyecto.data.model.Role
import com.example.registrox_proyecto.data.model.User

class AuthRepository {
    private val registeredUsers = mutableListOf<Pair<String, String>>()

    fun login(email: String, password: String): User? {
        val match = registeredUsers.find { it.first == email.trim() && it.second == password }
        return if (match != null) {
            val role = if (email.trim().lowercase().endsWith("@registrox.cl")) {
                Role.TRABAJADOR
            } else {
                Role.USUARIO
            }
            User(email.trim(), role)
        } else {
            null
        }
    }

    fun register(email: String, password: String): User? {
        val cleanEmail = email.trim()
        if (cleanEmail.isBlank() || password.isBlank()) return null
        if (password.length < 6) return null
        if (registeredUsers.any { it.first == cleanEmail }) return null

        val role = if (cleanEmail.lowercase().endsWith("@registrox.cl")) {
            Role.TRABAJADOR
        } else {
            Role.USUARIO
        }

        registeredUsers.add(cleanEmail to password)
        return User(cleanEmail, role)
    }

    fun logout(): Boolean {
        return true
    }
}