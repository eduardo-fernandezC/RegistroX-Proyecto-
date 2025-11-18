package com.example.registrox_proyecto.ui.components.topbar

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class TopBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()


    @Test
    fun defaultTopBar_muestraTituloCorrecto() {
        composeTestRule.setContent {
            DefaultTopBar(title = "Pantalla Prueba")
        }

        composeTestRule.onNodeWithText("Pantalla Prueba").assertIsDisplayed()
    }

    @Test
    fun defaultTopBar_muestraIconoBackCuandoTieneCallback() {
        composeTestRule.setContent {
            DefaultTopBar(
                title = "Pantalla Prueba",
                onBackClick = {}
            )
        }

        composeTestRule.onNode(hasContentDescription("Atras")).assertIsDisplayed()
    }

    @Test
    fun defaultTopBar_noMuestraIconoBackCuandoCallbackEsNulo() {
        composeTestRule.setContent {
            DefaultTopBar(
                title = "Pantalla Prueba",
                onBackClick = null
            )
        }

        composeTestRule.onNode(hasContentDescription("Atras")).assertDoesNotExist()
    }

    @Test
    fun defaultTopBar_onBackClickSeEjecuta() {
        var clicked = false

        composeTestRule.setContent {
            DefaultTopBar(
                title = "Pantalla Prueba",
                onBackClick = { clicked = true }
            )
        }

        composeTestRule.onNode(hasContentDescription("Atras")).performClick()

        assert(clicked)
    }


    @Test
    fun homeTopBar_muestraTituloRegistroX() {
        composeTestRule.setContent {
            HomeTopBar(carritoCount = 0)
        }

        composeTestRule.onNodeWithText("RegistroX").assertIsDisplayed()
    }

    @Test
    fun homeTopBar_muestraCarrito() {
        composeTestRule.setContent {
            HomeTopBar(carritoCount = 0)
        }

        composeTestRule.onNode(hasContentDescription("Carrito")).assertIsDisplayed()
    }

    @Test
    fun homeTopBar_muestraBadgeCuandoHayItems() {
        composeTestRule.setContent {
            HomeTopBar(carritoCount = 3)
        }

        composeTestRule.onNodeWithText("3").assertIsDisplayed()
    }

    @Test
    fun homeTopBar_onCarritoClickSeEjecuta() {
        var clicked = false

        composeTestRule.setContent {
            HomeTopBar(
                carritoCount = 1,
                onCarritoClick = { clicked = true }
            )
        }

        composeTestRule.onNode(hasContentDescription("Carrito")).performClick()

        assert(clicked)
    }

    @Test
    fun trabajadorTopBar_muestraTextosCorrectos() {
        composeTestRule.setContent {
            TrabajadorTopBar(onExitClick = {})
        }

        composeTestRule.onNodeWithText("Bienvenido").assertIsDisplayed()
        composeTestRule.onNodeWithText("Trabajador").assertIsDisplayed()
    }
}
