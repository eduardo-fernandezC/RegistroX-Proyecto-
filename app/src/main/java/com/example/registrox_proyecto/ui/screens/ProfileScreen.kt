package com.example.registrox_proyecto.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.registrox_proyecto.data.model.Role
import com.example.registrox_proyecto.data.model.User
import com.example.registrox_proyecto.ui.viewmodel.LoginViewModel
import com.example.registrox_proyecto.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    user: User,
    loginViewModel: LoginViewModel,
    navController: NavController,
    profileViewModel: ProfileViewModel
) {
    val imageUri by profileViewModel.imageUriState

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let { profileViewModel.saveImage(it.toString()) }
        }
    )

    val perfilTexto = when (user.role) {
        Role.TRABAJADOR -> "Perfil de trabajador"
        else -> "Perfil de usuario"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        if (imageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(12.dp))
            Button(onClick = { pickImageLauncher.launch("image/*") }) {
                Text("Cambiar foto de perfil")
            }
        } else {
            Button(
                onClick = { pickImageLauncher.launch("image/*") },
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Agregar foto")
            }
        }

        Spacer(Modifier.height(30.dp))

        Text(perfilTexto, style = MaterialTheme.typography.titleLarge)
        Text(user.email, style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(40.dp))

        Button(
            onClick = {
                profileViewModel.clearImage()
                loginViewModel.logout()
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Cerrar sesion")
        }
    }
}
