package com.example.registrox_proyecto.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import com.example.registrox_proyecto.data.model.Entrada
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenTestable(
    entradas: List<Entrada>,
    mensaje: String,
    carrito: List<Entrada>,
    onAgregar: (Entrada) -> Unit,
    onComprar: () -> Unit,
    onEliminar: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("Buscar entradas...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        val filtradas = remember(entradas, searchText) {
            if (searchText.isBlank()) entradas
            else entradas.filter { it.titulo.contains(searchText, ignoreCase = true) }
        }

        when {
            entradas.isEmpty() && mensaje.isNotEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(mensaje)
                }
            }

            filtradas.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.testTag("loading"),
                        strokeWidth = 4.dp
                    )
                }
            }

            else -> {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(filtradas) { entrada ->
                        // Simulación de EntradaCard real
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                                .testTag("entrada_${entrada.id}")
                        ) {
                            Text(entrada.titulo)
                            Button(
                                onClick = { onAgregar(entrada) },
                                enabled = entrada.estado.lowercase() == "disponible"
                            ) {
                                Text("Agregar")
                            }
                        }
                    }
                }
            }
        }

        if (carrito.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onComprar,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Comprar (${carrito.size})")
                }

                Button(
                    onClick = onEliminar,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Eliminar")
                }
            }
        }
    }
}

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val entradaDisponible = Entrada(
        id = 1L,
        titulo = "Concierto Rock",
        lugar = "Santiago",
        precio = 15000.0,
        estado = "disponible"
    )

    private val entradaOcupada = Entrada(
        id = 2L,
        titulo = "Teatro Infantil",
        lugar = "Talagante",
        precio = 9000.0,
        estado = "ocupada"
    )

    @Test
    fun homeScreen_muestraEntradas() {
        composeTestRule.setContent {
            HomeScreenTestable(
                entradas = listOf(entradaDisponible, entradaOcupada),
                mensaje = "",
                carrito = emptyList(),
                onAgregar = {},
                onComprar = {},
                onEliminar = {}
            )
        }

        composeTestRule.onNodeWithText("Concierto Rock").assertIsDisplayed()
        composeTestRule.onNodeWithText("Teatro Infantil").assertIsDisplayed()
    }

    @Test
    fun homeScreen_filtraEntradas() {
        composeTestRule.setContent {
            HomeScreenTestable(
                entradas = listOf(entradaDisponible, entradaOcupada),
                mensaje = "",
                carrito = emptyList(),
                onAgregar = {},
                onComprar = {},
                onEliminar = {}
            )
        }

        composeTestRule.onNodeWithText("Buscar entradas...").performTextInput("rock")

        composeTestRule.onNodeWithText("Concierto Rock").assertIsDisplayed()
        composeTestRule.onNodeWithText("Teatro Infantil").assertDoesNotExist()
    }

    @Test
    fun homeScreen_muestraLoadingCuandoFiltradoVacio() {
        composeTestRule.setContent {
            HomeScreenTestable(
                entradas = listOf(entradaDisponible),
                mensaje = "",
                carrito = emptyList(),
                onAgregar = {},
                onComprar = {},
                onEliminar = {}
            )
        }

        composeTestRule.onNodeWithText("Buscar entradas...").performTextInput("asdfgh")

        composeTestRule.onNodeWithTag("loading").assertIsDisplayed()
    }

    @Test
    fun homeScreen_agregarEntradaDisponible() {
        var added = false

        composeTestRule.setContent {
            HomeScreenTestable(
                entradas = listOf(entradaDisponible),
                mensaje = "",
                carrito = emptyList(),
                onAgregar = { added = true },
                onComprar = {},
                onEliminar = {}
            )
        }

        composeTestRule.onNodeWithText("Agregar").performClick()

        assert(added)
    }

    @Test
    fun homeScreen_noAgregaEntradaOcupada() {
        var added = false

        composeTestRule.setContent {
            HomeScreenTestable(
                entradas = listOf(entradaOcupada),
                mensaje = "",
                carrito = emptyList(),
                onAgregar = { added = true },
                onComprar = {},
                onEliminar = {}
            )
        }

        composeTestRule
            .onNodeWithText("Agregar")
            .assertIsNotEnabled()
    }

    @Test
    fun homeScreen_muestraBotonesCarrito() {
        composeTestRule.setContent {
            HomeScreenTestable(
                entradas = listOf(entradaDisponible),
                mensaje = "",
                carrito = listOf(entradaDisponible),
                onAgregar = {},
                onComprar = {},
                onEliminar = {}
            )
        }

        composeTestRule.onNodeWithText("Comprar (1)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Eliminar").assertIsDisplayed()
    }

    @Test
    fun homeScreen_clickComprarEjecutaCallback() {
        var clicked = false

        composeTestRule.setContent {
            HomeScreenTestable(
                entradas = listOf(entradaDisponible),
                mensaje = "",
                carrito = listOf(entradaDisponible),
                onAgregar = {},
                onComprar = { clicked = true },
                onEliminar = {}
            )
        }

        composeTestRule.onNodeWithText("Comprar (1)").performClick()

        assert(clicked)
    }

    @Test
    fun homeScreen_clickEliminarEjecutaCallback() {
        var clicked = false

        composeTestRule.setContent {
            HomeScreenTestable(
                entradas = listOf(entradaDisponible),
                mensaje = "",
                carrito = listOf(entradaDisponible),
                onAgregar = {},
                onComprar = {},
                onEliminar = { clicked = true }
            )
        }

        composeTestRule.onNodeWithText("Eliminar").performClick()

        assert(clicked)
    }
}
