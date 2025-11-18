package com.example.registrox_proyecto.ui.components.cards

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.registrox_proyecto.data.model.Entrada
import com.example.registrox_proyecto.ui.components.cards.EntradaCard
import org.junit.Rule
import org.junit.Test

class EntradaCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val entradaFake = Entrada(
        id = 1L,
        titulo = "Concierto de Prueba",
        lugar = "Teatro Municipal",
        precio = 19990.0,
        estado = "disponible",
        cantidad = 1,
        usuarioEmail = "test@example.com"
    )

    @Test
    fun entradaCard_muestraDatosCorrectos() {
        composeTestRule.setContent {
            EntradaCard(entrada = entradaFake)
        }

        composeTestRule.onNodeWithText("Concierto de Prueba").assertIsDisplayed()
        composeTestRule.onNodeWithText("Teatro Municipal").assertIsDisplayed()
        composeTestRule.onNodeWithText("Precio: 19990.0 $").assertIsDisplayed()
    }

    @Test
    fun entradaCard_botonAgregarHabilitadoCuandoDisponible() {
        composeTestRule.setContent {
            EntradaCard(entrada = entradaFake)
        }

        composeTestRule.onNodeWithText("Agregar").assertIsDisplayed()
        composeTestRule.onNodeWithText("Agregar").assertIsEnabled()
    }

    @Test
    fun entradaCard_botonAgregarDeshabilitadoCuandoNoDisponible() {
        val entradaNoDisponible = entradaFake.copy(estado = "agotado")

        composeTestRule.setContent {
            EntradaCard(entrada = entradaNoDisponible)
        }

        composeTestRule.onNodeWithText("Agregar").assertIsDisplayed()
        composeTestRule.onNodeWithText("Agregar").assertIsNotEnabled()
    }

    @Test
    fun entradaCard_onAgregarSeEjecuta() {
        var clicked = false

        composeTestRule.setContent {
            EntradaCard(
                entrada = entradaFake,
                onAgregar = { clicked = true }
            )
        }

        composeTestRule.onNodeWithText("Agregar").performClick()

        assert(clicked)
    }
}
