package com.example.registrox_proyecto.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.registrox_proyecto.data.scannerqr.ScannerActivity
import com.example.registrox_proyecto.ui.viewmodel.CarritoViewModel
import com.example.registrox_proyecto.utils.NetworkUtils
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

@Composable
fun HomeTrabajadorScreen(
    onBackClick: () -> Unit = {},
    carritoViewModel: CarritoViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var codigoEscaneado by remember { mutableStateOf("") }
    val mensajeOperacion = carritoViewModel.mensajeOperacion.value
    val context = LocalContext.current

    val qrLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            codigoEscaneado = result.contents
            if (NetworkUtils.isNetworkAvailable(context)) {
                carritoViewModel.marcarEntradaUsadaRemota(codigoEscaneado)
            } else {
                carritoViewModel.mensajeOperacion.value = "Sin conexión a internet"
            }
        } else {
            carritoViewModel.mensajeOperacion.value = "No se detectó ningún código"
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = codigoEscaneado,
                onValueChange = { codigoEscaneado = it },
                label = { Text("Ingrese código QR o ID") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            Button(onClick = {
                val options = ScanOptions().apply {
                    setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                    setPrompt("Apunta al QR para escanear")
                    setCameraId(0)
                    setBeepEnabled(true)
                    setOrientationLocked(true)
                    setCaptureActivity(ScannerActivity::class.java)
                }
                qrLauncher.launch(options)
            }) {
                Text("Escanear QR")
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (codigoEscaneado.isNotEmpty()) {
                        if (NetworkUtils.isNetworkAvailable(context)) {
                            carritoViewModel.marcarEntradaUsadaRemota(codigoEscaneado)
                        } else {
                            carritoViewModel.mensajeOperacion.value = "Sin conexión a internet"
                        }
                    } else {
                        carritoViewModel.mensajeOperacion.value = "Debe ingresar o escanear un código"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Validar Manual")
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = mensajeOperacion,
                style = MaterialTheme.typography.titleMedium,
                color = if (mensajeOperacion.contains("correctamente", true))
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}
