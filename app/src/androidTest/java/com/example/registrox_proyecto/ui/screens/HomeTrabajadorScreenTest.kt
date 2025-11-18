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
fun HomeTrabajadorScreenTestable(
    codigoInicial: String = "",
    mensaje: String = "",
    onCodigoChange: (String) -> Unit,
    onScanClick: () -> Unit,
    onValidarManual: (String) -> Unit
) {
    var codigo by remember { mutableStateOf(codigoInicial) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedTextField(
            value = codigo,
            onValueChange = {
                codigo = it
                onCodigoChange(it)
            },
            label = { Text("Ingrese código QR o ID") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("codigoInput")
        )

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = onScanClick,
            modifier = Modifier.testTag("scanButton")
        ) {
            Text("Escanear QR")
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { onValidarManual(codigo) },
            modifier = Modifier.testTag("validarButton"),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("Validar Manual")
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = mensaje,
            modifier = Modifier
                .padding(8.dp)
                .testTag("mensaje"),
            color = if (mensaje.contains("correctamente", true))
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.error
        )
    }
}

class HomeTrabajadorScreenTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun homeTrabajador_muestraCampoCodigo() {
        rule.setContent {
            HomeTrabajadorScreenTestable(
                onCodigoChange = {},
                onScanClick = {},
                onValidarManual = {}
            )
        }

        rule.onNodeWithTag("codigoInput").assertIsDisplayed()
    }

    @Test
    fun homeTrabajador_muestraBotones() {
        rule.setContent {
            HomeTrabajadorScreenTestable(
                onCodigoChange = {},
                onScanClick = {},
                onValidarManual = {}
            )
        }

        rule.onNodeWithTag("scanButton").assertIsDisplayed()
        rule.onNodeWithTag("validarButton").assertIsDisplayed()
    }

    @Test
    fun homeTrabajador_editaCodigoCorrectamente() {
        var valorRecibido = ""

        rule.setContent {
            HomeTrabajadorScreenTestable(
                onCodigoChange = { valorRecibido = it },
                onScanClick = {},
                onValidarManual = {}
            )
        }

        rule.onNodeWithTag("codigoInput")
            .performTextInput("QR999")

        assert(valorRecibido == "QR999")
    }

    @Test
    fun homeTrabajador_clickScanLlamaCallback() {
        var clicked = false

        rule.setContent {
            HomeTrabajadorScreenTestable(
                onCodigoChange = {},
                onScanClick = { clicked = true },
                onValidarManual = {}
            )
        }

        rule.onNodeWithTag("scanButton").performClick()

        assert(clicked)
    }

    @Test
    fun homeTrabajador_validarManualLlamaCallbackConCodigo() {
        var codigoValidado = ""

        rule.setContent {
            HomeTrabajadorScreenTestable(
                codigoInicial = "ABC123",
                onCodigoChange = {},
                onScanClick = {},
                onValidarManual = { codigoValidado = it }
            )
        }

        rule.onNodeWithTag("validarButton").performClick()

        assert(codigoValidado == "ABC123")
    }

    @Test
    fun homeTrabajador_muestraMensajeError() {
        rule.setContent {
            HomeTrabajadorScreenTestable(
                mensaje = "Sin conexión",
                onCodigoChange = {},
                onScanClick = {},
                onValidarManual = {}
            )
        }

        rule.onNodeWithTag("mensaje")
            .assertIsDisplayed()
            .assertTextContains("Sin conexión")
    }

    @Test
    fun homeTrabajador_muestraMensajeExito() {
        rule.setContent {
            HomeTrabajadorScreenTestable(
                mensaje = "Entrada validada correctamente",
                onCodigoChange = {},
                onScanClick = {},
                onValidarManual = {}
            )
        }

        rule.onNodeWithTag("mensaje")
            .assertIsDisplayed()
            .assertTextContains("correctamente", substring = true)
    }

}
