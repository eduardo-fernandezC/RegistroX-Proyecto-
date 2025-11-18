package com.example.registrox_proyecto.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.registrox_proyecto.data.model.CompraEntrada
import com.example.registrox_proyecto.data.model.Entrada
import org.junit.Rule
import org.junit.Test

@Composable
fun TicketCardTestable(
    ticket: CompraEntrada,
    onClick: () -> Unit
) {
    val colorEstado = when (ticket.estado.lowercase()) {
        "ocupada" -> MaterialTheme.colorScheme.error
        "caducada" -> MaterialTheme.colorScheme.outline
        else -> MaterialTheme.colorScheme.primary
    }

    val textoEstado = when (ticket.estado.lowercase()) {
        "ocupada" -> "Ocupada"
        "caducada" -> "Caducada"
        else -> "Disponible para escaneo"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() }
            .testTag("ticket_${ticket.codigoQR}"),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                ticket.entrada?.titulo ?: "Evento desconocido",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                ticket.entrada?.lugar ?: "Lugar no disponible",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("[QR]", modifier = Modifier.testTag("qr_placeholder"))

            Spacer(modifier = Modifier.height(8.dp))
            Text("Código: ${ticket.codigoQR ?: "No disponible"}")

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(textoEstado, color = colorEstado, fontWeight = FontWeight.Bold)

                Text(
                    "Precio: ${ticket.entrada?.precio ?: 0.0} $",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntradasScreenTestable(
    tickets: List<CompraEntrada>,
    mensaje: String,
    isLoading: Boolean,
    onClickTicket: (String) -> Unit,
    onEliminar: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        if (mensaje.contains("error", true)) {
            Text(
                text = mensaje,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.testTag("loading"))
                }
            }

            tickets.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tienes entradas compradas")
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("lista_tickets")
                ) {
                    items(tickets) { ticket ->
                        TicketCardTestable(
                            ticket = ticket,
                            onClick = { ticket.codigoQR?.let { onClickTicket(it) } }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onEliminar,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar entradas usadas")
                }
            }
        }
    }
}

class EntradasScreenTest {

    @get:Rule
    val rule = createComposeRule()

    private val entrada = Entrada(
        id = 1L,
        titulo = "Concierto Rock",
        lugar = "Santiago",
        precio = 12000.0,
        estado = "disponible"
    )

    private val ticketDisponible = CompraEntrada(
        id = 10L,
        estado = "disponible",
        codigoQR = "QR123",
        entrada = entrada
    )

    private val ticketOcupado = CompraEntrada(
        id = 11L,
        estado = "ocupada",
        codigoQR = "QR999",
        entrada = entrada
    )

    @Test
    fun entradasScreen_muestraLoading() {
        rule.setContent {
            EntradasScreenTestable(
                tickets = emptyList(),
                mensaje = "",
                isLoading = true,
                onClickTicket = {},
                onEliminar = {}
            )
        }

        rule.onNodeWithTag("loading").assertIsDisplayed()
    }

    @Test
    fun entradasScreen_muestraMensajeSinEntradas() {
        rule.setContent {
            EntradasScreenTestable(
                tickets = emptyList(),
                mensaje = "",
                isLoading = false,
                onClickTicket = {},
                onEliminar = {}
            )
        }

        rule.onNodeWithText("No tienes entradas compradas").assertIsDisplayed()
    }

    @Test
    fun entradasScreen_muestraMensajeDeError() {
        rule.setContent {
            EntradasScreenTestable(
                tickets = emptyList(),
                mensaje = "Error de conexión",
                isLoading = false,
                onClickTicket = {},
                onEliminar = {}
            )
        }

        rule.onNodeWithText("Error de conexión").assertIsDisplayed()
    }

    @Test
    fun entradasScreen_muestraTickets() {
        rule.setContent {
            EntradasScreenTestable(
                tickets = listOf(ticketDisponible, ticketOcupado),
                mensaje = "",
                isLoading = false,
                onClickTicket = {},
                onEliminar = {}
            )
        }

        // Verificamos por TAG único, no por texto
        rule.onNodeWithTag("ticket_QR123").assertIsDisplayed()
        rule.onNodeWithTag("ticket_QR999").assertIsDisplayed()
    }


    @Test
    fun entradasScreen_clickEnTicketEjecutaCallback() {
        var codigoNavegado: String? = null

        rule.setContent {
            EntradasScreenTestable(
                tickets = listOf(ticketDisponible),
                mensaje = "",
                isLoading = false,
                onClickTicket = { codigoNavegado = it },
                onEliminar = {}
            )
        }

        rule.onNodeWithTag("ticket_QR123").performClick()

        assert(codigoNavegado == "QR123")
    }

    @Test
    fun entradasScreen_muestraEstadoOcupada() {
        rule.setContent {
            EntradasScreenTestable(
                tickets = listOf(ticketOcupado),
                mensaje = "",
                isLoading = false,
                onClickTicket = {},
                onEliminar = {}
            )
        }

        rule.onNodeWithText("Ocupada").assertIsDisplayed()
    }

    @Test
    fun entradasScreen_clickEliminarLlamaCallback() {
        var eliminado = false

        rule.setContent {
            EntradasScreenTestable(
                tickets = listOf(ticketDisponible),
                mensaje = "",
                isLoading = false,
                onClickTicket = {},
                onEliminar = { eliminado = true }
            )
        }

        rule.onNodeWithText("Eliminar entradas usadas").performClick()

        assert(eliminado)
    }
}
