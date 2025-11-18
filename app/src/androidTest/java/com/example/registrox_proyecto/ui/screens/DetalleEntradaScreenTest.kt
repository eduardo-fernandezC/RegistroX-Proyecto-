package com.example.registrox_proyecto.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleEntradaScreenTestable(

    codigoQR: String,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tu entrada") },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("backButton")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text("[QR]", modifier = Modifier.testTag("qrPlaceholder"))

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Codigo: $codigoQR",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("codigoTexto")
            )
        }
    }
}

class DetalleEntradaScreenTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun detalleEntrada_muestraTitulo() {
        rule.setContent {
            DetalleEntradaScreenTestable(codigoQR = "QR123") {}
        }

        rule.onNodeWithText("Tu entrada").assertIsDisplayed()
    }

    @Test
    fun detalleEntrada_muestraQRPlaceholder() {
        rule.setContent {
            DetalleEntradaScreenTestable(codigoQR = "QR123") {}
        }

        rule.onNodeWithTag("qrPlaceholder").assertIsDisplayed()
    }

    @Test
    fun detalleEntrada_muestraCodigoCorrecto() {
        rule.setContent {
            DetalleEntradaScreenTestable(codigoQR = "QR12345") {}
        }

        rule.onNodeWithTag("codigoTexto")
            .assertIsDisplayed()
            .assertTextContains("Codigo: QR12345")
    }

    @Test
    fun detalleEntrada_botonVolverEjecutaCallback() {
        var backPressed = false

        rule.setContent {
            DetalleEntradaScreenTestable(
                codigoQR = "QR999",
                onBack = { backPressed = true }
            )
        }

        rule.onNodeWithTag("backButton").performClick()

        assert(backPressed)
    }
}
