package com.example.registrox_proyecto.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.registrox_proyecto.ui.viewmodel.ComprasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComprasScreen(
    navController: NavController,
    viewModel: ComprasViewModel
) {
    val compras by viewModel.compras.collectAsStateWithLifecycle()
    val mensaje by viewModel.mensaje.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.cargarCompras()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Historial de Compras") }) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            when {
                compras.isNotEmpty() -> {
                    LazyColumn {
                        items(compras) { compra ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Compra #${compra.id ?: "-"}",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(text = "Fecha: ${compra.fechaCompra ?: "No especificada"}")
                                    Text(text = "Usuario: ${compra.usuario.email}")
                                    Spacer(Modifier.height(8.dp))

                                    val entradas = compra.compraEntradas ?: emptyList()
                                    Text(text = "Entradas (${entradas.size}):")

                                    entradas.forEach { ce ->
                                        val entrada = ce.entrada
                                        if (entrada != null) {
                                            Text(
                                                text = "- ${entrada.titulo} (${entrada.lugar})",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Text(
                                                text = "   QR: ${ce.codigoQR ?: "Sin QR"} | Estado: ${ce.estado}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                mensaje.isNotEmpty() -> Text(
                    text = mensaje,
                    modifier = Modifier.align(Alignment.Center)
                )

                else -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
