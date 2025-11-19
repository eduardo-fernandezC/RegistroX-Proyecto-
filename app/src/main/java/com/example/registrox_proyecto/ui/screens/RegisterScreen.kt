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
import com.example.registrox_proyecto.ui.components.Net.InternetGuard
import com.example.registrox_proyecto.ui.viewmodel.RegisterViewModel
import com.example.registrox_proyecto.ui.viewmodel.RegisterResult
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel
) {

    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val registerResult by viewModel.registerResult.collectAsStateWithLifecycle()
    val shouldNavigate by viewModel.shouldNavigate.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(registerResult) {
        when (registerResult) {
            is RegisterResult.Success -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        "Cuenta creada con exito Puedes iniciar sesion ahora.",
                        duration = SnackbarDuration.Long
                    )
                }
            }

            is RegisterResult.Error -> {
                scope.launch {
                    val error = (registerResult as RegisterResult.Error).message
                    snackbarHostState.showSnackbar(error)
                }
            }

            else -> Unit
        }
        viewModel.clearRegisterResult()
    }

    LaunchedEffect(shouldNavigate) {
        if (shouldNavigate) {
            navController.navigate("login") {
                popUpTo("register") { inclusive = true }
            }
            viewModel.resetNavigation()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            InternetGuard {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Crear cuenta", style = MaterialTheme.typography.headlineSmall)

                    OutlinedTextField(
                        value = formState.email,
                        onValueChange = { viewModel.onEmailChange(it) },
                        label = { Text("Correo") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        isError = formState.emailError != null
                    )

                    formState.emailError?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }

                    var passwordVisible by remember { mutableStateOf(false) }
                    var confirmPasswordVisible by remember { mutableStateOf(false) }

                    OutlinedTextField(
                        value = formState.password,
                        onValueChange = { viewModel.onPasswordChange(it) },
                        label = { Text("Contraseña") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val icon =
                                if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(icon, contentDescription = null)
                            }
                        },
                        isError = formState.passwordError != null
                    )

                    formState.passwordError?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }

                    OutlinedTextField(
                        value = formState.confirmPassword,
                        onValueChange = { viewModel.onConfirmPasswordChange(it) },
                        label = { Text("Confirmar Contraseña") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val icon =
                                if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = {
                                confirmPasswordVisible = !confirmPasswordVisible
                            }) {
                                Icon(icon, contentDescription = null)
                            }
                        },
                        isError = formState.confirmPasswordError != null
                    )

                    formState.confirmPasswordError?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }

                    Button(
                        onClick = { viewModel.register() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = formState.isValid && !isLoading
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 20.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Registrarse")
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (registerResult is RegisterResult.Success) {
                                viewModel.allowNavigation()
                            } else {
                                navController.popBackStack()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("Volver al Login")
                    }
                }
            }
        }
    }
}
