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
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import com.example.registrox_proyecto.data.model.LoginFormState
import org.junit.Rule
import org.junit.Test

@Composable
fun LoginScreenTestable(
    formState: LoginFormState,
    isLoading: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text("Bienvenido a RegistroX")

        OutlinedTextField(
            value = formState.email,
            onValueChange = onEmailChange,
            label = { Text("Correo") },
            isError = formState.emailError != null,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        formState.emailError?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        OutlinedTextField(
            value = formState.password,
            onValueChange = onPasswordChange,
            label = { Text("Contraseña") },
            isError = formState.passwordError != null,
            singleLine = true,
            visualTransformation =
                if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
            trailingIcon = {
                val icon =
                    if (passwordVisible) Icons.Default.Visibility
                    else Icons.Default.VisibilityOff

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(icon, contentDescription = null)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        formState.passwordError?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        if (formState.loginError.isNotEmpty()) {
            Text(text = formState.loginError, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = onLoginClick,
            enabled = formState.isValid && !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Iniciar Sesion")
            }
        }

        TextButton(onClick = onRegisterClick) {
            Text("¿No tienes cuenta? Registrate")
        }
    }
}

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_muestraCamposCorrectamente() {
        composeTestRule.setContent {
            LoginScreenTestable(
                formState = LoginFormState(),
                isLoading = false,
                onEmailChange = {},
                onPasswordChange = {},
                onLoginClick = {},
                onRegisterClick = {}
            )
        }

        composeTestRule.onNodeWithText("Bienvenido a RegistroX").assertIsDisplayed()
        composeTestRule.onNodeWithText("Correo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Contraseña").assertIsDisplayed()
        composeTestRule.onNodeWithText("Iniciar Sesion").assertIsDisplayed()
        composeTestRule.onNodeWithText("¿No tienes cuenta? Registrate").assertIsDisplayed()
    }

    @Test
    fun loginScreen_muestraErrorEnEmail() {
        val state = LoginFormState(
            email = "malo",
            emailError = "Formato de email inválido"
        )

        composeTestRule.setContent {
            LoginScreenTestable(
                formState = state,
                isLoading = false,
                onEmailChange = {},
                onPasswordChange = {},
                onLoginClick = {},
                onRegisterClick = {}
            )
        }

        composeTestRule.onNodeWithText("Formato de email inválido").assertIsDisplayed()
    }

    @Test
    fun loginScreen_botonDeshabilitadoCuandoFormularioInvalido() {
        composeTestRule.setContent {
            LoginScreenTestable(
                formState = LoginFormState(isValid = false),
                isLoading = false,
                onEmailChange = {},
                onPasswordChange = {},
                onLoginClick = {},
                onRegisterClick = {}
            )
        }

        composeTestRule.onNodeWithText("Iniciar Sesion").assertIsNotEnabled()
    }

    @Test
    fun loginScreen_clickEnRegistrateEjecutaCallback() {
        var clicked = false

        composeTestRule.setContent {
            LoginScreenTestable(
                formState = LoginFormState(),
                isLoading = false,
                onEmailChange = {},
                onPasswordChange = {},
                onLoginClick = {},
                onRegisterClick = { clicked = true }
            )
        }

        composeTestRule.onNodeWithText("¿No tienes cuenta? Registrate").performClick()

        assert(clicked)
    }
}
