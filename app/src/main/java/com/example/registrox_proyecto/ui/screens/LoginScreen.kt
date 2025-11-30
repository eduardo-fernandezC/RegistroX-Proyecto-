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
import com.example.registrox_proyecto.navigation.Routes
import com.example.registrox_proyecto.ui.components.Net.InternetGuard
import com.example.registrox_proyecto.ui.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel) {

    val user by viewModel.user.collectAsStateWithLifecycle()
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(user) {
        if (user != null && viewModel.justLoggedIn) {

            navController.navigate(Routes.OTP) {
                popUpTo(Routes.LOGIN) { inclusive = true }
            }

            viewModel.justLoggedIn = false
        }
    }

    var passVisible by remember { mutableStateOf(false) }

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
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = formState.password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
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

                if (formState.loginError.isNotEmpty()) {
                    Text(formState.loginError, color = MaterialTheme.colorScheme.error)
                }

                Button(
                    onClick = { viewModel.login() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    if (isLoading)
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    else
                        Text("Iniciar sesión")
                }

                TextButton(onClick = { navController.navigate(Routes.REGISTER) }) {
                    Text("¿No tienes cuenta? Regístrate")
                }
            }
        }
    }
}
