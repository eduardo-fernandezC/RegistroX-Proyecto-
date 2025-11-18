package com.example.registrox_proyecto.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.registrox_proyecto.data.model.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComprasScreenTestable(
    compras: List<Compra>,
    mensaje: String,
    isLoading: Boolean
) {
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
                    LazyColumn(
                        modifier = Modifier.testTag("comprasLista")
                    ) {
                        items(compras) { compra ->

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .testTag("compra_${compra.id}")
                            ) {
                                Column(Modifier.padding(16.dp)) {

                                    Text(
                                        text = "Compra #${compra.id ?: "-"}",
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    Text("Fecha: ${compra.fechaCompra ?: "No especificada"}")

                                    Text("Usuario: ${compra.usuario.email}")

                                    Spacer(Modifier.height(8.dp))

                                    val entradas = compra.compraEntradas ?: emptyList()
                                    Text("Entradas (${entradas.size}):")

                                    entradas.forEach { ce ->
                                        val entrada = ce.entrada
                                        if (entrada != null) {
                                            Text("- ${entrada.titulo} (${entrada.lugar})")
                                            Text(
                                                "QR: ${ce.codigoQR ?: "Sin QR"} | Estado: ${ce.estado}",
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

                mensaje.isNotEmpty() -> {
                    Text(
                        text = mensaje,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .testTag("mensaje"),
                    )
                }

                else -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .testTag("loading")
                    )
                }
            }
        }
    }
}

class ComprasScreenTest {

    @get:Rule
    val rule = createComposeRule()

    private val usuarioTest = Usuario(
        id = 10L,
        email = "cliente@test.cl",
        password = null,
        rol = Rol(id = 1, nombre = "USUARIO")
    )

    private val entradaTest = Entrada(
        id = 20L,
        titulo = "Concierto Rock",
        lugar = "Santiago",
        precio = 15000.0,
        estado = "disponible",
        codigoQR = "QR123"
    )

    private val compraEntradaTest = CompraEntrada(
        id = 30L,
        estado = "disponible",
        codigoQR = "QR123",
        entrada = entradaTest
    )

    private val compraTest = Compra(
        id = 40L,
        fechaCompra = "2024-10-20",
        usuario = usuarioTest,
        compraEntradas = listOf(compraEntradaTest)
    )

    @Test
    fun comprasScreen_muestraLoading() {
        rule.setContent {
            ComprasScreenTestable(
                compras = emptyList(),
                mensaje = "",
                isLoading = true
            )
        }

        rule.onNodeWithTag("loading").assertIsDisplayed()
    }

    @Test
    fun comprasScreen_muestraMensaje() {
        rule.setContent {
            ComprasScreenTestable(
                compras = emptyList(),
                mensaje = "Error al cargar",
                isLoading = false
            )
        }

        rule.onNodeWithTag("mensaje")
            .assertIsDisplayed()
            .assertTextContains("Error al cargar")
    }

    @Test
    fun comprasScreen_muestraListaDeCompras() {
        rule.setContent {
            ComprasScreenTestable(
                compras = listOf(compraTest),
                mensaje = "",
                isLoading = false
            )
        }

        rule.onNodeWithTag("comprasLista").assertIsDisplayed()
        rule.onNodeWithTag("compra_40").assertIsDisplayed()
    }

    @Test
    fun comprasScreen_muestraDatosCompra() {
        rule.setContent {
            ComprasScreenTestable(
                compras = listOf(compraTest),
                mensaje = "",
                isLoading = false
            )
        }

        rule.onNodeWithText("Compra #40").assertIsDisplayed()
        rule.onNodeWithText("Fecha: 2024-10-20").assertIsDisplayed()
        rule.onNodeWithText("Usuario: cliente@test.cl").assertIsDisplayed()
    }

    @Test
    fun comprasScreen_muestraEntradasDeCompra() {
        rule.setContent {
            ComprasScreenTestable(
                compras = listOf(compraTest),
                mensaje = "",
                isLoading = false
            )
        }

        rule.onNodeWithText("Entradas (1):").assertIsDisplayed()
        rule.onNodeWithText("- Concierto Rock (Santiago)").assertIsDisplayed()
        rule.onNodeWithText("QR: QR123 | Estado: disponible").assertIsDisplayed()
    }
}
