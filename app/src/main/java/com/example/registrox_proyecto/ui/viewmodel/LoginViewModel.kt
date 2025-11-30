package com.example.registrox_proyecto.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.registrox_proyecto.data.datastore.AuthDataStore
import com.example.registrox_proyecto.data.model.LoginFormState
import com.example.registrox_proyecto.data.model.Role
import com.example.registrox_proyecto.data.model.User
import com.example.registrox_proyecto.data.repository.AuthRepository
import com.example.registrox_proyecto.utils.NetworkUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    application: Application,
    private val authRepository: AuthRepository,
    private val authDataStore: AuthDataStore
) : AndroidViewModel(application) {

    var justLoggedIn = false

    private val _formState = MutableStateFlow(LoginFormState())
    val formState: StateFlow<LoginFormState> = _formState

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            val email = authDataStore.email.first() ?: ""
            val role = authDataStore.role.first() ?: ""

            if (email.isNotBlank() && role.isNotBlank()) {
                _user.value = User(
                    id = 0L,
                    email = email,
                    role = if (role == "TRABAJADOR") Role.TRABAJADOR else Role.USUARIO
                )
            }
        }
    }

    fun onEmailChange(value: String) {
        _formState.update { it.copy(email = value, emailError = null) }
    }

    fun onPasswordChange(value: String) {
        _formState.update { it.copy(password = value, passwordError = null) }
    }

    fun login() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(800)

            try {
                val email = _formState.value.email.trim()
                val password = _formState.value.password

                val usuarioAPI = authRepository.login(email, password)

                if (usuarioAPI != null) {

                    val roleEnum =
                        if (usuarioAPI.rol.id?.toInt() == 1) Role.TRABAJADOR else Role.USUARIO

                    justLoggedIn = true

                    _user.value = User(
                        id = usuarioAPI.id ?: 0L,
                        email = usuarioAPI.email,
                        role = roleEnum
                    )

                    authDataStore.saveUser(usuarioAPI.email, roleEnum.name)

                } else {
                    _formState.update { it.copy(loginError = "Credenciales incorrectas") }
                }

            } catch (e: Exception) {
                _formState.update { it.copy(loginError = "Error: ${e.localizedMessage}") }
            }

            _isLoading.value = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            authDataStore.saveUser("", "")
            _user.value = null
            justLoggedIn = false
            _formState.value = LoginFormState()
        }
    }
}
