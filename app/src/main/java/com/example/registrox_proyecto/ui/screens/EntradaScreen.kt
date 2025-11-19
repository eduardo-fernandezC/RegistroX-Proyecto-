package com.example.registrox_proyecto.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.registrox_proyecto.data.model.CompraEntrada
import com.example.registrox_proyecto.navigation.Routes
import com.example.registrox_proyecto.ui.components.Net.InternetGuard
import com.example.registrox_proyecto.ui.viewmodel.CarritoViewModel
import com.example.registrox_proyecto.utils.NetworkUtils
import com.example.registrox_proyecto.utils.QRCodeGenerator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntradasScreen(
    navController: NavController,
    carritoViewModel: CarritoViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val tickets = carritoViewModel.ticketsUsuario
    val mensaje = carritoViewModel.mensajeOperacion.value
    val isLoading by carritoViewModel.isLoading
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            if (NetworkUtils.isNetworkAvailable(context)) {
                carritoViewModel.actualizarUsuario()
            } else {
                carritoViewModel.mensajeOperacion.value = "Sin conexion a internet"
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mis Entradas") }) }
    ) { padding ->
        InternetGuard {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                if (mensaje.contains("Error", true)) {
                    Text(
                        text = mensaje,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (tickets.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No tienes entradas compradas")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 8.dp)
                    ) {
                        items(tickets) { ticket ->
                            TicketCard(
                                ticket = ticket,
                                onClick = {
                                    ticket.codigoQR?.let { codigo ->
                                        navController.navigate("${Routes.DETALLE}/$codigo")
                                    }
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { carritoViewModel.eliminarOcupadas() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Eliminar entradas usadas")
                    }
                }
            }
        }
    }
}

@Composable
fun TicketCard(
    ticket: CompraEntrada,
    onClick: () -> Unit
) {
    val colorEstado = when (ticket.estado.lowercase()) {
        "ocupada" -> Color.Red
        "caducada" -> Color.Gray
        else -> Color(0xFF4CAF50)
    }
    val textoEstado = when (ticket.estado.lowercase()) {
        "ocupada" -> "Ocupada"
        "caducada" -> "Caducada"
        else -> "Disponible para escaneo"
    }
    val qrBitmap = remember(ticket.codigoQR) {
        ticket.codigoQR?.let { QRCodeGenerator.generateQRCode(it, 450) }
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
            Text(
                text = ticket.entrada?.titulo ?: "Evento desconocido",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = ticket.entrada?.lugar ?: "Lugar no disponible",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            qrBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Codigo QR del ticket",
                    modifier = Modifier
                        .size(220.dp)
                        .padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Codigo: ${ticket.codigoQR ?: "No disponible"}")
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
                Text(
                    "Precio: ${ticket.entrada?.precio ?: 0.0} $",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
