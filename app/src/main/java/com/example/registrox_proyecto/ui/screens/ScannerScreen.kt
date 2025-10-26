package com.example.registrox_proyecto.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.registrox_proyecto.scannerqr.ScannerActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(onBackClick: () -> Unit, onScanResult: (String) -> Unit) {
    var mensaje by remember { mutableStateOf("Apunta la camara al codigo QR de la entrada") }

    val qrLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            onScanResult(result.contents)
            mensaje = "Entrada detectada"
        } else {
            mensaje = "No se detecto ningun codigo"
        }
    }

    Scaffold(
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(mensaje, style = MaterialTheme.typography.titleMedium)
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
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = { mensaje = "Introduce manualmente el codigo si procede." }) {
                Text("Validacion Manual")
            }
        }
    }
}
