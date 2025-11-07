package com.example.registrox_proyecto.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.registrox_proyecto.data.scannerqr.ScannerActivity
import com.example.registrox_proyecto.ui.viewmodel.CarritoViewModel
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

@Composable
fun HomeTrabajadorScreen(
    onBackClick: () -> Unit = {},
    carritoViewModel: CarritoViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var codigoEscaneado by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf<String?>(null) }

    val qrLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            codigoEscaneado = result.contents

            val resultadoOk = carritoViewModel.marcarComoUsadaPorCodigo(codigoEscaneado)


            resultado = if (resultadoOk) {
                " Entrada $codigoEscaneado marcada como caducada"
            } else {
                "No se encontro ninguna entrada con el codigo $codigoEscaneado"
            }
        } else {
            resultado = " No se detecto ningun codigo"
        }
    }


    Scaffold(
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = codigoEscaneado,
                onValueChange = { codigoEscaneado = it },
                label = { Text("Ingrese codigo QR o ID") },
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
                        val resultadoOk = carritoViewModel.marcarComoUsadaPorCodigo(codigoEscaneado)

                        resultado = if (resultadoOk) {
                            "Entrada $codigoEscaneado marcada como caducada"
                        } else {
                            "No se encontro ninguna entrada con el código $codigoEscaneado"
                        }
                    } else {
                        resultado = "Debe ingresar o escanear un codigo"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Validar Manual")
            }


            Spacer(Modifier.height(24.dp))

            resultado?.let {
                Text(it, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
