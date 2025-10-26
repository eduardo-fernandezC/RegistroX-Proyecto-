package com.example.registrox_proyecto.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registrox_proyecto.data.datastore.AuthDataStore
import com.example.registrox_proyecto.data.model.LoginFormState
import com.example.registrox_proyecto.data.model.Role
import com.example.registrox_proyecto.data.model.User
import com.example.registrox_proyecto.data.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val authDataStore: AuthDataStore
) : ViewModel() {

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
                _user.value = User(savedEmail, roleEnum)
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
            email.isBlank() -> "El email no puede estar vacío"
            !regex.matches(email) -> "Formato de email inválido"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        val regex = Regex("^(?=.*[A-Z])(?=.*\\d).{6,}$")
        return when {
            password.isBlank() -> "La contraseña no puede estar vacía"
            !regex.matches(password) -> "Debe tener al menos 6 caracteres, una mayúscula y un número"
            else -> null
        }
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
                _formState.update { it.copy(loginError = "Formulario inválido") }
                return@launch
            }

            _isLoading.value = true
            delay(2000)

            val result = authRepository.login(_formState.value.email, _formState.value.password)

            if (result != null) {
                _user.value = result
                _formState.update { it.copy(loginError = "") }

                authDataStore.saveUser(result.email, result.role.name)
            } else {
                _formState.update { it.copy(loginError = "Correo o contraseña inválidos") }
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
