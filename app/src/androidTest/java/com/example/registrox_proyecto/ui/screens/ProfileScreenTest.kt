package com.example.registrox_proyecto.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.registrox_proyecto.data.model.Role
import com.example.registrox_proyecto.data.model.User
import org.junit.Rule
import org.junit.Test

@Composable
fun ProfileScreenTestable(
    user: User,
    imageUrl: String?,
    onPickImage: () -> Unit,
    onLogout: () -> Unit
) {
    val perfilTexto = when (user.role) {
        Role.TRABAJADOR -> "Perfil de trabajador"
        else -> "Perfil de usuario"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // IMAGEN / ICONO
        if (!imageUrl.isNullOrEmpty()) {
            // Simulación de imagen reemplazando AsyncImage
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .testTag("profileImage")
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onPickImage,
                modifier = Modifier.testTag("changePhotoButton")
            ) { Text("Cambiar foto de perfil") }

        } else {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Sin imagen",
                modifier = Modifier
                    .size(140.dp)
                    .testTag("defaultIcon"),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onPickImage,
                modifier = Modifier.testTag("addPhotoButton")
            ) { Text("Agregar foto de perfil") }
        }

        Spacer(Modifier.height(30.dp))
        Text(perfilTexto, style = MaterialTheme.typography.titleLarge)
        Text(user.email, style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(40.dp))

        Button(
            onClick = onLogout,
            modifier = Modifier.testTag("logoutButton"),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Cerrar sesión")
        }
    }
}

class ProfileScreenTest {

    @get:Rule
    val rule = createComposeRule()

    private val userTrabajador = User(
        id = 1L,
        email = "test@registrox.cl",
        role = Role.TRABAJADOR
    )

    private val userNormal = User(
        id = 2L,
        email = "cliente@gmail.com",
        role = Role.USUARIO
    )

    @Test
    fun profileScreen_muestraIconoPorDefecto() {
        rule.setContent {
            ProfileScreenTestable(
                user = userNormal,
                imageUrl = null,
                onPickImage = {},
                onLogout = {}
            )
        }
        rule.onNodeWithTag("defaultIcon").assertIsDisplayed()
    }

    @Test
    fun profileScreen_muestraFotoDePerfilCuandoHayImagen() {
        rule.setContent {
            ProfileScreenTestable(
                user = userNormal,
                imageUrl = "https://foto.png",
                onPickImage = {},
                onLogout = {}
            )
        }
        rule.onNodeWithTag("profileImage").assertIsDisplayed()
    }

    @Test
    fun profileScreen_muestraPerfilDeTrabajador() {
        rule.setContent {
            ProfileScreenTestable(
                user = userTrabajador,
                imageUrl = null,
                onPickImage = {},
                onLogout = {}
            )
        }
        rule.onNodeWithText("Perfil de trabajador").assertIsDisplayed()
    }

    @Test
    fun profileScreen_muestraPerfilDeUsuario() {
        rule.setContent {
            ProfileScreenTestable(
                user = userNormal,
                imageUrl = null,
                onPickImage = {},
                onLogout = {}
            )
        }
        rule.onNodeWithText("Perfil de usuario").assertIsDisplayed()
    }

    @Test
    fun profileScreen_muestraEmail() {
        rule.setContent {
            ProfileScreenTestable(
                user = userNormal,
                imageUrl = null,
                onPickImage = {},
                onLogout = {}
            )
        }
        rule.onNodeWithText("cliente@gmail.com").assertIsDisplayed()
    }

    @Test
    fun profileScreen_clickAgregarFotoEjecutaCallback() {
        var clicked = false

        rule.setContent {
            ProfileScreenTestable(
                user = userNormal,
                imageUrl = null,
                onPickImage = { clicked = true },
                onLogout = {}
            )
        }

        rule.onNodeWithTag("addPhotoButton").performClick()

        assert(clicked)
    }

    @Test
    fun profileScreen_clickLogoutEjecutaCallback() {
        var loggedOut = false

        rule.setContent {
            ProfileScreenTestable(
                user = userNormal,
                imageUrl = null,
                onPickImage = {},
                onLogout = { loggedOut = true }
            )
        }

        rule.onNodeWithTag("logoutButton").performClick()

        assert(loggedOut)
    }
}
