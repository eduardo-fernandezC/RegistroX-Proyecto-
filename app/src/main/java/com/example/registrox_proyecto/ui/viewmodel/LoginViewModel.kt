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

    private val _formState = MutableStateFlow(LoginFormState())
    val formState: StateFlow<LoginFormState> = _formState

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            val savedEmail = authDataStore.email.first() ?: ""
            val savedRole = authDataStore.role.first() ?: ""
            if (savedEmail.isNotBlank() && savedRole.isNotBlank()) {
                val roleEnum = if (savedRole == "TRABAJADOR") Role.TRABAJADOR else Role.USUARIO
                _user.value = User(
                    id = 0L,
                    email = savedEmail,
                    role = roleEnum
                )
            }
        }
    }

    fun onEmailChange(value: String) {
        _formState.update { state ->
            val error = validateEmail(value)
            state.copy(
                email = value,
                emailError = error,
                isValid = validateForm(state.copy(email = value, emailError = error))
            )
        }
    }

    fun onPasswordChange(value: String) {
        _formState.update { state ->
            val error = validatePassword(value)
            state.copy(
                password = value,
                passwordError = error,
                isValid = validateForm(state.copy(password = value, passwordError = error))
            )
        }
    }

    private fun validateEmail(email: String): String? {
        val regex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
        return when {
            email.isBlank() -> "El email no puede estar vacio"
            !regex.matches(email) -> "Formato de email invalido"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return if (password.isBlank()) "La contraseña no puede estar vacia" else null
    }

    private fun validateForm(state: LoginFormState): Boolean {
        return state.emailError == null &&
                state.passwordError == null &&
                state.email.isNotBlank() &&
                state.password.isNotBlank()
    }

    fun login() {
        viewModelScope.launch {
            if (!_formState.value.isValid) {
                _formState.update { it.copy(loginError = "Formulario invalido") }
                return@launch
            }

            _isLoading.value = true
            delay(800)

            try {
                val context = getApplication<Application>().applicationContext
                if (!NetworkUtils.isNetworkAvailable(context)) {
                    _formState.update { it.copy(loginError = "Sin conexion a internet") }
                    _isLoading.value = false
                    return@launch
                }

                val email = _formState.value.email.trim()
                val password = _formState.value.password

                val usuarioAPI = authRepository.login(email, password)

                if (usuarioAPI != null) {
                    val roleEnum = when {
                        usuarioAPI.email.endsWith("@registrox.cl", ignoreCase = true) -> Role.TRABAJADOR
                        usuarioAPI.rol.id?.toInt() == 1 -> Role.TRABAJADOR
                        else -> Role.USUARIO
                    }

                    _user.value = User(
                        id = usuarioAPI.id ?: 0L,
                        email = usuarioAPI.email,
                        role = roleEnum
                    )
                    _formState.update { it.copy(loginError = "") }
                    authDataStore.saveUser(usuarioAPI.email, roleEnum.name)

                    Log.d("LOGIN", "Usuario logueado: ${usuarioAPI.email}, Rol: $roleEnum")
                } else {
                    _formState.update { it.copy(loginError = "Correo no encontrado o incorrecto") }
                }

            } catch (e: Exception) {
                _formState.update { it.copy(loginError = "Error de conexion: ${e.localizedMessage}") }
                Log.e("LOGIN_ERROR", "Error en login: ${e.localizedMessage}")
            }

            _isLoading.value = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            authDataStore.saveUser("", "")
            _user.value = null
            _formState.value = LoginFormState()
        }
    }
}
