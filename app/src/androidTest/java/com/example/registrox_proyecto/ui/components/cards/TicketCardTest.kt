package com.example.registrox_proyecto.ui.components.cards


import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.registrox_proyecto.data.model.CompraEntrada
import com.example.registrox_proyecto.data.model.Entrada
import com.example.registrox_proyecto.ui.screens.TicketCard
import org.junit.Rule
import org.junit.Test

class TicketCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val entradaFake = Entrada(
        id = 1L,
        titulo = "Festival Latino",
        lugar = "Estadio Nacional",
        precio = 35000.0,
        estado = "disponible",
        codigoQR = "QR123",
        usuarioEmail = "test@example.com"
    )

    private val ticketFake = CompraEntrada(
        id = 10L,
        estado = "disponible",
        codigoQR = "QR123",
        entrada = entradaFake
    )

    @Test
    fun ticketCard_muestraDatosCorrectos() {
        composeTestRule.setContent {
            TicketCard(ticket = ticketFake, onClick = {})
        }

        composeTestRule.onNodeWithText("Festival Latino").assertIsDisplayed()
        composeTestRule.onNodeWithText("Estadio Nacional").assertIsDisplayed()
        composeTestRule.onNodeWithText("Código: QR123").assertIsDisplayed()
        composeTestRule.onNodeWithText("Precio: 35000.0 $").assertIsDisplayed()
    }

    @Test
    fun ticketCard_muestraEstadoCorrectoDisponible() {
        composeTestRule.setContent {
            TicketCard(ticket = ticketFake, onClick = {})
        }

        composeTestRule.onNodeWithText("Disponible para escaneo").assertIsDisplayed()
    }

    @Test
    fun ticketCard_muestraEstadoCorrectoOcupada() {
        val ticket = ticketFake.copy(estado = "ocupada")

        composeTestRule.setContent {
            TicketCard(ticket = ticket, onClick = {})
        }

        composeTestRule.onNodeWithText("Ocupada").assertIsDisplayed()
    }

    @Test
    fun ticketCard_muestraEstadoCorrectoCaducada() {
        val ticket = ticketFake.copy(estado = "caducada")

        composeTestRule.setContent {
            TicketCard(ticket = ticket, onClick = {})
        }

        composeTestRule.onNodeWithText("Caducada").assertIsDisplayed()
    }

    @Test
    fun ticketCard_onClickSeEjecuta() {
        var clicked = false

        composeTestRule.setContent {
            TicketCard(
                ticket = ticketFake,
                onClick = { clicked = true }
            )
        }

        composeTestRule.onNodeWithText("Festival Latino").performClick()

        assert(clicked)
    }
}
