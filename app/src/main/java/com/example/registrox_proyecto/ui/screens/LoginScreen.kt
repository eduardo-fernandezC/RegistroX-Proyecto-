package com.example.registrox_proyecto.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.registrox_proyecto.data.model.Role
import com.example.registrox_proyecto.navigation.Routes
import com.example.registrox_proyecto.ui.components.Net.InternetGuard
import com.example.registrox_proyecto.ui.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel) {

    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val user by viewModel.user.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(user) {
        if (user != null) {
            navController.navigate(Routes.OTP) {
                popUpTo(Routes.LOGIN) { inclusive = true }
            }
        }
    }

    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        InternetGuard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text("Bienvenido a RegistroX", style = MaterialTheme.typography.headlineSmall)

                OutlinedTextField(
                    value = formState.email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    label = { Text("Correo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = formState.emailError != null
                )

                formState.emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                OutlinedTextField(
                    value = formState.password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    isError = formState.passwordError != null
                )

                formState.passwordError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                if (formState.loginError.isNotEmpty()) {
                    Text(formState.loginError, color = MaterialTheme.colorScheme.error)
                }

                Button(
                    onClick = { viewModel.login() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = formState.isValid && !isLoading
                ) {
                    if (isLoading)
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    else
                        Text("Iniciar Sesión")
                }

                TextButton(onClick = { navController.navigate(Routes.REGISTER) }) {
                    Text("¿No tienes cuenta? Regístrate")
                }
            }
        }
    }
}
