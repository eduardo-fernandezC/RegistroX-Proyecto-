package com.example.registrox_proyecto.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Verified
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Home : BottomNavItem(Routes.HOME, "Explorar", Icons.Filled.Explore)
    object QR : BottomNavItem(Routes.ENTRADAS, "Entradas", Icons.Filled.ConfirmationNumber)
    object Profile : BottomNavItem(Routes.PROFILE, "Cuenta", Icons.Filled.Person)
    object Scan : BottomNavItem(Routes.TRABAJADOR, "Verificar", Icons.Filled.Verified)
}
