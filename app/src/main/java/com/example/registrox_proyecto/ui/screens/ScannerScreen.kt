package com.example.registrox_proyecto.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.registrox_proyecto.data.scannerqr.ScannerActivity
import com.example.registrox_proyecto.ui.viewmodel.ScannerViewModel
import com.example.registrox_proyecto.utils.NetworkUtils
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    onBackClick: () -> Unit,
    viewModel: ScannerViewModel = viewModel()
) {
    var mensaje by remember { mutableStateOf("Apunta la cámara al código QR de la entrada") }
    val context = LocalContext.current
    val qrLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            if (NetworkUtils.isNetworkAvailable(context)) {
                viewModel.validarEntrada(result.contents)
                mensaje = "Validando entrada..."
            } else {
                mensaje = "Sin conexión a internet"
            }
        } else {
            mensaje = "No se detectó ningún código"
        }
    }

    val mensajeOperacion by viewModel.mensajeOperacion

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = mensaje,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    val options = ScanOptions().apply {
                        setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                        setPrompt("Apunta al QR de la entrada para validar")
                        setCaptureActivity(ScannerActivity::class.java)
                        setBeepEnabled(true)
                        setBarcodeImageEnabled(true)
                        setOrientationLocked(true)
                    }
                    qrLauncher.launch(options)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Iniciar Escaneo")
            }

            Spacer(Modifier.height(16.dp))
            if (mensajeOperacion.isNotEmpty()) {
                Text(
                    text = mensajeOperacion,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (mensajeOperacion.contains("correctamente", ignoreCase = true))
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error
                )
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(onClick = { mensaje = "Introduce manualmente el código si procede." }) {
                Text("Validación Manual")
            }
        }
    }
}
