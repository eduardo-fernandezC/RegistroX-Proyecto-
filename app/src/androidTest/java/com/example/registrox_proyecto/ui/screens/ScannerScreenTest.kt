package com.example.registrox_proyecto.ui.screens

import androidx.compose.foundation.layout.*
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

@Composable
fun ScannerScreenTestable(
    mensajeInicial: String = "Apunta la cámara al código QR de la entrada",
    mensajeOperacion: String = "",
    onScanClick: () -> Unit,
    onManualClick: () -> Unit
) {
    var mensaje by remember { mutableStateOf(mensajeInicial) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = mensaje,
            modifier = Modifier.testTag("mensajePrincipal"),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = onScanClick,
            modifier = Modifier.testTag("scanButton"),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Iniciar Escaneo")
        }

        Spacer(Modifier.height(16.dp))

        if (mensajeOperacion.isNotEmpty()) {
            Text(
                text = mensajeOperacion,
                modifier = Modifier.testTag("mensajeOperacion"),
                style = MaterialTheme.typography.bodyLarge,
                color = if (mensajeOperacion.contains("correctamente", true))
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.error
            )
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                mensaje = "Introduce manualmente el código si procede."
                onManualClick()
            },
            modifier = Modifier.testTag("manualButton")
        ) {
            Text("Validación Manual")
        }
    }
}

class ScannerScreenTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun scanner_muestraMensajeInicial() {
        rule.setContent {
            ScannerScreenTestable(
                onScanClick = {},
                onManualClick = {}
            )
        }

        rule.onNodeWithTag("mensajePrincipal")
            .assert(hasText("Apunta", substring = true, ignoreCase = true))
    }

    @Test
    fun scanner_muestraBotones() {
        rule.setContent {
            ScannerScreenTestable(
                onScanClick = {},
                onManualClick = {}
            )
        }

        rule.onNodeWithTag("scanButton").assertIsDisplayed()
        rule.onNodeWithTag("manualButton").assertIsDisplayed()
    }

    @Test
    fun scanner_scanClickEjecutaCallback() {
        var clicked = false

        rule.setContent {
            ScannerScreenTestable(
                onScanClick = { clicked = true },
                onManualClick = {}
            )
        }

        rule.onNodeWithTag("scanButton").performClick()

        assert(clicked)
    }

    @Test
    fun scanner_manualCambiaMensaje() {
        var manualClicked = false

        rule.setContent {
            ScannerScreenTestable(
                onScanClick = {},
                onManualClick = { manualClicked = true }
            )
        }

        rule.onNodeWithTag("manualButton").performClick()

        assert(manualClicked)

        rule.onNodeWithTag("mensajePrincipal")
            .assert(hasText("Introduce manualmente", substring = true, ignoreCase = true))
    }

    @Test
    fun scanner_muestraMensajeError() {
        rule.setContent {
            ScannerScreenTestable(
                mensajeOperacion = "Sin conexión",
                onScanClick = {},
                onManualClick = {}
            )
        }

        rule.onNodeWithTag("mensajeOperacion")
            .assertIsDisplayed()
            .assertTextContains("Sin conexión")
    }

    @Test
    fun scanner_muestraMensajeExito() {
        rule.setContent {
            ScannerScreenTestable(
                mensajeOperacion = "Validado correctamente",
                onScanClick = {},
                onManualClick = {}
            )
        }

        rule.onNodeWithTag("mensajeOperacion")
            .assertIsDisplayed()
            .assertTextContains("correctamente", substring = true)
    }
}
