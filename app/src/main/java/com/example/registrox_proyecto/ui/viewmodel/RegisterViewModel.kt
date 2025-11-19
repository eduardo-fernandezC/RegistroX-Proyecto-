package com.example.registrox_proyecto.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.registrox_proyecto.data.model.RegisterFormState
import com.example.registrox_proyecto.data.model.Role
import com.example.registrox_proyecto.data.model.User
import com.example.registrox_proyecto.data.repository.AuthRepository
import com.example.registrox_proyecto.utils.NetworkUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    application: Application,
    private val authRepository: AuthRepository = AuthRepository()
) : AndroidViewModel(application) {

    private val _formState = MutableStateFlow(RegisterFormState())
    val formState: StateFlow<RegisterFormState> = _formState

    private val _registerResult = MutableStateFlow<RegisterResult?>(null)
    val registerResult: StateFlow<RegisterResult?> = _registerResult

    private val _shouldNavigate = MutableStateFlow(false)
    val shouldNavigate: StateFlow<Boolean> = _shouldNavigate

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

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

    fun onConfirmPasswordChange(value: String) {
        _formState.update { state ->
            val error = validateConfirmPassword(value, _formState.value.password)
            state.copy(
                confirmPassword = value,
                confirmPasswordError = error,
                isValid = validateForm(state.copy(confirmPassword = value, confirmPasswordError = error))
            )
        }
    }

    private fun validateEmail(email: String): String? {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return when {
            email.isBlank() -> "El correo no puede estar vacio"
            !emailRegex.matches(email) -> "Debe ingresar un correo valido"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        val regex = Regex("^(?=.*[A-Z])(?=.*\\d).{6,}$")
        return when {
            password.isBlank() -> "La contraseña no puede estar vacia"
            !regex.matches(password) -> "Debe tener al menos 6 caracteres, una mayuscula y un numero"
            else -> null
        }
    }

    private fun validateConfirmPassword(confirmPassword: String, password: String): String? {
        return when {
            confirmPassword.isBlank() -> "Debe confirmar la contraseña"
            confirmPassword != password -> "Las contraseñas no coinciden"
            else -> null
        }
    }

    private fun validateForm(state: RegisterFormState): Boolean {
        return state.emailError == null &&
                state.passwordError == null &&
                state.confirmPasswordError == null &&
                state.email.isNotBlank() &&
                state.password.isNotBlank() &&
                state.confirmPassword.isNotBlank()
    }

    fun register() {
        viewModelScope.launch {
            if (!_formState.value.isValid) {
                _registerResult.value = RegisterResult.Error("Formulario invalido")
                return@launch
            }

            _isLoading.value = true
            delay(1000)

            val context = getApplication<Application>().applicationContext
            if (!NetworkUtils.isNetworkAvailable(context)) {
                _registerResult.value = RegisterResult.Error("Sin conexion a internet")
                _isLoading.value = false
                return@launch
            }

            val email = _formState.value.email.trim()
            val password = _formState.value.password

            try {
                val creado = authRepository.register(email, password)
                if (creado) {
                    val roleEnum = if (email.endsWith("@registrox.cl"))
                        Role.TRABAJADOR else Role.USUARIO

                    val user = User(
                        id = 0L,
                        email = email,
                        role = roleEnum
                    )
                    _registerResult.value = RegisterResult.Success(user)
                } else {
                    _registerResult.value = RegisterResult.Error("Error al registrar el usuario")
                }
            } catch (e: Exception) {
                _registerResult.value = RegisterResult.Error("Error de conexion con el servidor")
            }

            _isLoading.value = false
        }
    }

    fun allowNavigation() {
        _shouldNavigate.value = true
    }

    fun resetNavigation() {
        _shouldNavigate.value = false
    }

    fun clearRegisterResult() {
        _registerResult.value = null
    }
}

sealed class RegisterResult {
    data class Success(val user: User) : RegisterResult()
    data class Error(val message: String) : RegisterResult()
}
