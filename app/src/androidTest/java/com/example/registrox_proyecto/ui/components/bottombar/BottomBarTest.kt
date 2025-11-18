package com.example.registrox_proyecto.ui.components.bottombar

import androidx.compose.material3.Text
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.registrox_proyecto.data.model.Role
import com.example.registrox_proyecto.navigation.BottomNavItem
import com.example.registrox_proyecto.navigation.Routes
import org.junit.Rule
import org.junit.Test

class BottomBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun bottomBar_muestraSoloOpcionesParaUsuarioNormal() {
        composeTestRule.setContent {
            val navController = rememberNavController()

            BottomBar(
                navController = navController,
                items = listOf(
                    BottomNavItem.Home,
                    BottomNavItem.QR,
                    BottomNavItem.Profile,
                    BottomNavItem.Scan
                ),
                userRole = Role.USUARIO
            )
        }

        composeTestRule.onNodeWithText("Explorar").assertIsDisplayed()
        composeTestRule.onNodeWithText("Entradas").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cuenta").assertIsDisplayed()

        composeTestRule.onNodeWithText("Verificar").assertDoesNotExist()
    }

    @Test
    fun bottomBar_muestraOpcionTrabajadorCuandoRolEsTrabajador() {
        composeTestRule.setContent {
            val navController = rememberNavController()

            BottomBar(
                navController = navController,
                items = listOf(
                    BottomNavItem.Home,
                    BottomNavItem.QR,
                    BottomNavItem.Profile,
                    BottomNavItem.Scan
                ),
                userRole = Role.TRABAJADOR
            )
        }

        composeTestRule.onNodeWithText("Verificar").assertIsDisplayed()
    }

    @Test
    fun bottomBar_navegaAEntradasCorrectamente() {
        composeTestRule.setContent {
            val navController = rememberNavController()

            androidx.compose.material3.Scaffold(
                bottomBar = {
                    BottomBar(
                        navController = navController,
                        items = listOf(
                            BottomNavItem.Home,
                            BottomNavItem.QR,
                            BottomNavItem.Profile
                        ),
                        userRole = Role.USUARIO
                    )
                }
            ) { padding ->
                NavHost(
                    navController = navController,
                    startDestination = Routes.HOME
                ) {
                    composable(Routes.HOME) { Text("Pantalla Home") }
                    composable(Routes.ENTRADAS) { Text("Pantalla Entradas") }
                    composable(Routes.PROFILE) { Text("Pantalla Perfil") }
                }
            }
        }

        composeTestRule.onNodeWithText("Entradas").performClick()

        composeTestRule.onNodeWithText("Pantalla Entradas").assertIsDisplayed()
    }

    @Test
    fun bottomBar_navegaAProfileCorrectamente() {
        composeTestRule.setContent {
            val navController = rememberNavController()

            androidx.compose.material3.Scaffold(
                bottomBar = {
                    BottomBar(
                        navController = navController,
                        items = listOf(
                            BottomNavItem.Home,
                            BottomNavItem.QR,
                            BottomNavItem.Profile
                        ),
                        userRole = Role.USUARIO
                    )
                }
            ) { padding ->
                NavHost(
                    navController = navController,
                    startDestination = Routes.HOME
                ) {
                    composable(Routes.HOME) { Text("Pantalla Home") }
                    composable(Routes.ENTRADAS) { Text("Pantalla Entradas") }
                    composable(Routes.PROFILE) { Text("Pantalla Perfil") }
                }
            }
        }

        composeTestRule.onNodeWithText("Cuenta").performClick()

        composeTestRule.onNodeWithText("Pantalla Perfil").assertIsDisplayed()
    }
}
