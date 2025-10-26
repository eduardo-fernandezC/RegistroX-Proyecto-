package com.example.registrox_proyecto.data.model

data class LoginFormState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val loginError: String = "",
    val isValid: Boolean = false
)