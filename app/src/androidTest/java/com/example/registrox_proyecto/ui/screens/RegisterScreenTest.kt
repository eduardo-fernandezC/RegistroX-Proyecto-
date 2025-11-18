package com.example.registrox_proyecto.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.registrox_proyecto.data.model.RegisterFormState
import com.example.registrox_proyecto.data.model.Role
import com.example.registrox_proyecto.data.model.User
import com.example.registrox_proyecto.ui.viewmodel.RegisterResult
import org.junit.Rule
import org.junit.Test

@Composable
fun RegisterScreenTestable(
    formState: RegisterFormState,
    isLoading: Boolean,
    registerResult: RegisterResult?,
    shouldNavigate: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onBackClick: () -> Unit,
    onResetNavigation: () -> Unit,
    onClearRegisterResult: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(registerResult) {
        when (registerResult) {
            is RegisterResult.Success -> {
                snackbarHostState.showSnackbar(
                    "Cuenta creada con exito Puedes iniciar sesion ahora."
                )
            }
            is RegisterResult.Error -> {
                snackbarHostState.showSnackbar(registerResult.message)
            }
            null -> Unit
        }

        if (registerResult != null) {
            onClearRegisterResult()
        }
    }

    LaunchedEffect(shouldNavigate) {
        if (shouldNavigate) {
            onResetNavigation()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text("Crear cuenta", style = MaterialTheme.typography.headlineSmall)

            OutlinedTextField(
                value = formState.email,
                onValueChange = onEmailChange,
                label = { Text("Correo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = formState.emailError != null
            )

            formState.emailError?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            var passVisible by remember { mutableStateOf(false) }

            OutlinedTextField(
                value = formState.password,
                onValueChange = onPasswordChange,
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = formState.passwordError != null,
                visualTransformation =
                    if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passVisible = !passVisible }) {
                        Icon(
                            if (passVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            null
                        )
                    }
                }
            )

            formState.passwordError?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            var confirmVisible by remember { mutableStateOf(false) }

            OutlinedTextField(
                value = formState.confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = { Text("Confirmar Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = formState.confirmPasswordError != null,
                visualTransformation =
                    if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { confirmVisible = !confirmVisible }) {
                        Icon(
                            if (confirmVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            null
                        )
                    }
                }
            )

            formState.confirmPasswordError?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = onRegisterClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = formState.isValid && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp))
                } else {
                    Text("Registrarse")
                }
            }

            Button(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Volver al Login")
            }
        }
    }
}

class RegisterScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun registerScreen_muestraCamposCorrectamente() {
        composeTestRule.setContent {
            RegisterScreenTestable(
                formState = RegisterFormState(),
                isLoading = false,
                registerResult = null,
                shouldNavigate = false,
                onEmailChange = {},
                onPasswordChange = {},
                onConfirmPasswordChange = {},
                onRegisterClick = {},
                onBackClick = {},
                onResetNavigation = {},
                onClearRegisterResult = {}
            )
        }

        composeTestRule.onNodeWithText("Crear cuenta").assertIsDisplayed()
        composeTestRule.onNodeWithText("Correo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Contraseña").assertIsDisplayed()
        composeTestRule.onNodeWithText("Confirmar Contraseña").assertIsDisplayed()
        composeTestRule.onNodeWithText("Registrarse").assertIsDisplayed()
        composeTestRule.onNodeWithText("Volver al Login").assertIsDisplayed()
    }

    @Test
    fun registerScreen_muestraErrores() {
        composeTestRule.setContent {
            RegisterScreenTestable(
                formState = RegisterFormState(
                    emailError = "Debe ingresar un correo válido",
                    passwordError = "Debe tener al menos 6 caracteres, una mayúscula y un número",
                    confirmPasswordError = "Las contraseñas no coinciden"
                ),
                isLoading = false,
                registerResult = null,
                shouldNavigate = false,
                onEmailChange = {},
                onPasswordChange = {},
                onConfirmPasswordChange = {},
                onRegisterClick = {},
                onBackClick = {},
                onResetNavigation = {},
                onClearRegisterResult = {}
            )
        }

        composeTestRule.onNodeWithText("Debe ingresar un correo válido").assertIsDisplayed()
        composeTestRule.onNodeWithText("Debe tener al menos 6 caracteres, una mayúscula y un número")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Las contraseñas no coinciden").assertIsDisplayed()
    }

    @Test
    fun registerScreen_muestraSnackbarDeSuccess() {
        composeTestRule.setContent {
            RegisterScreenTestable(
                formState = RegisterFormState(),
                isLoading = false,
                registerResult = RegisterResult.Success(
                    User(id = 1L, email = "test@correo.cl", role = Role.USUARIO)
                ),
                shouldNavigate = false,
                onEmailChange = {},
                onPasswordChange = {},
                onConfirmPasswordChange = {},
                onRegisterClick = {},
                onBackClick = {},
                onResetNavigation = {},
                onClearRegisterResult = {}
            )
        }

        composeTestRule.onNodeWithText("Cuenta creada con exito Puedes iniciar sesion ahora.")
            .assertIsDisplayed()
    }

    @Test
    fun registerScreen_muestraSnackbarDeError() {
        composeTestRule.setContent {
            RegisterScreenTestable(
                formState = RegisterFormState(),
                isLoading = false,
                registerResult = RegisterResult.Error("Error de registro"),
                shouldNavigate = false,
                onEmailChange = {},
                onPasswordChange = {},
                onConfirmPasswordChange = {},
                onRegisterClick = {},
                onBackClick = {},
                onResetNavigation = {},
                onClearRegisterResult = {}
            )
        }

        composeTestRule.onNodeWithText("Error de registro").assertIsDisplayed()
    }

    @Test
    fun registerScreen_navegaCuandoShouldNavigateEsTrue() {
        var navigated = false

        composeTestRule.setContent {
            RegisterScreenTestable(
                formState = RegisterFormState(),
                isLoading = false,
                registerResult = null,
                shouldNavigate = true,
                onEmailChange = {},
                onPasswordChange = {},
                onConfirmPasswordChange = {},
                onRegisterClick = {},
                onBackClick = {},
                onResetNavigation = { navigated = true },
                onClearRegisterResult = {}
            )
        }

        assert(navigated)
    }

    @Test
    fun registerScreen_clickEnVolverEjecutaCallback() {
        var clicked = false

        composeTestRule.setContent {
            RegisterScreenTestable(
                formState = RegisterFormState(),
                isLoading = false,
                registerResult = null,
                shouldNavigate = false,
                onEmailChange = {},
                onPasswordChange = {},
                onConfirmPasswordChange = {},
                onRegisterClick = {},
                onBackClick = { clicked = true },
                onResetNavigation = {},
                onClearRegisterResult = {}
            )
        }

        composeTestRule.onNodeWithText("Volver al Login").performClick()

        assert(clicked)
    }
}
