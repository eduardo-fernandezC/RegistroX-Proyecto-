package com.example.registrox_proyecto.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.registrox_proyecto.data.model.Entrada
import com.example.registrox_proyecto.navigation.Routes
import com.example.registrox_proyecto.ui.viewmodel.CarritoViewModel
import com.example.registrox_proyecto.utils.QRCodeGenerator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntradasScreen(
    navController: NavController,
    carritoViewModel: CarritoViewModel
) {
    val entradas = carritoViewModel.entradasCompradas

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mis Entradas") })
        }
    ) { padding ->
        if (entradas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No tienes entradas compradas")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                items(entradas) { entrada ->
                    EntradaCompradaCard(
                        entrada = entrada,
                        onClick = {
                            entrada.codigoQR?.let { codigo ->
                                navController.navigate("${Routes.DETALLE}/$codigo")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EntradaCompradaCard(
    entrada: Entrada,
    onClick: () -> Unit
) {
    val colorEstado = when (entrada.estado.lowercase()) {
        "ocupada" -> Color.Red
        "caducada" -> Color.Gray
        else -> Color(0xFF4CAF50)
    }

    val textoEstado = when (entrada.estado.lowercase()) {
        "ocupada" -> "Ocupada"
        "caducada" -> "Caducada"
        else -> "Disponible para escaneo"
    }

    val qrBitmap = remember(entrada.codigoQR) {
        entrada.codigoQR?.let { QRCodeGenerator.generateQRCode(it, 450) }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(entrada.titulo, style = MaterialTheme.typography.titleLarge)
            Text(entrada.lugar, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))

            qrBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Codigo QR de la entrada",
                    modifier = Modifier
                        .size(220.dp)
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Codigo: ${entrada.codigoQR ?: "No disponible"}")
            Text("Comprado por: ${entrada.usuarioEmail}", color = Color.DarkGray)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    textoEstado,
                    color = colorEstado,
                    fontWeight = FontWeight.Bold
                )
                Text("Precio: ${entrada.precio} $")
            }
        }
    }
}
