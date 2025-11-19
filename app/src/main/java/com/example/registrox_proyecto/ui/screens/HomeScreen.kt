package com.example.registrox_proyecto.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.registrox_proyecto.navigation.Routes
import com.example.registrox_proyecto.ui.components.Net.InternetGuard
import com.example.registrox_proyecto.ui.components.cards.EntradaCard
import com.example.registrox_proyecto.ui.viewmodel.CarritoViewModel
import com.example.registrox_proyecto.ui.viewmodel.EntradasApiViewModel
import com.example.registrox_proyecto.utils.NetworkUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    carritoViewModel: CarritoViewModel,
    viewModel: EntradasApiViewModel = EntradasApiViewModel()
) {
    var searchText by remember { mutableStateOf("") }

    val entradas by viewModel.entradas.collectAsStateWithLifecycle()
    val mensaje by viewModel.mensaje.collectAsStateWithLifecycle()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val hasInternet = NetworkUtils.isNetworkAvailable(context)
        viewModel.cargarEntradas(hasInternet)
    }

    Scaffold { padding ->

        InternetGuard {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Buscar entradas...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    singleLine = true
                )

                val entradasFiltradas = remember(entradas, searchText) {
                    if (searchText.isBlank()) entradas
                    else entradas.filter {
                        it.titulo.contains(searchText, ignoreCase = true)
                    }
                }

                when {
                    entradas.isEmpty() && mensaje.isNotEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = mensaje,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    entradasFiltradas.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    else -> {
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(entradasFiltradas) { entrada ->
                                EntradaCard(
                                    entrada = entrada,
                                    onAgregar = { carritoViewModel.agregar(it) }
                                )
                            }
                        }
                    }
                }

                if (carritoViewModel.carrito.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                carritoViewModel.comprar()
                                navController.navigate(Routes.ENTRADAS) {
                                    popUpTo(Routes.HOME) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Comprar (${carritoViewModel.carrito.size})")
                        }

                        Button(
                            onClick = { carritoViewModel.limpiar() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Text("Eliminar")
                        }
                    }
                }
            }
        }
    }
}
