package com.example.registrox_proyecto.ui.components.cards

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.registrox_proyecto.data.model.Entrada

@Composable
fun EntradaCard(
    entrada: Entrada,
    onAgregar: (Entrada) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(entrada.titulo, style = MaterialTheme.typography.titleLarge)
            Text(entrada.lugar, style = MaterialTheme.typography.bodyMedium)
            Text("Precio: ${entrada.precio} $", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { onAgregar(entrada) },
                    enabled = entrada.estado.lowercase() == "disponible",
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Agregar", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
