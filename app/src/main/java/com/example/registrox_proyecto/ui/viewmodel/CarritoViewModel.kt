package com.example.registrox_proyecto.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registrox_proyecto.data.datastore.EntradasDataStore
import com.example.registrox_proyecto.data.datastore.AuthDataStore
import com.example.registrox_proyecto.data.model.Entrada
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.random.Random

class CarritoViewModel(
    private val dataStore: EntradasDataStore,
    private val authDataStore: AuthDataStore? = null
) : ViewModel() {

    val carrito = mutableStateListOf<Entrada>()
    val entradasCompradas = mutableStateListOf<Entrada>()
    val mensajeOperacion = mutableStateOf("")

    private var usuarioActual: String? = null

    init {
        viewModelScope.launch {
            recargarEntradasUsuario()
        }
    }

    private suspend fun recargarEntradasUsuario() {
        val email = authDataStore?.email?.firstOrNull()?.trim().orEmpty()
        if (email.isBlank()) {
            entradasCompradas.clear()
            dataStore.clearEntradas("anonimo")
            return
        }

        usuarioActual = email
        dataStore.entradasPorUsuario(email).collect { guardadas ->
            entradasCompradas.clear()
            entradasCompradas.addAll(guardadas)
        }
    }


    fun agregar(entrada: Entrada) {
        val existente = carrito.find { it.id == entrada.id }
        if (existente != null) {
            val actualizado = existente.copy(cantidad = existente.cantidad + entrada.cantidad)
            carrito.remove(existente)
            carrito.add(actualizado)
        } else {
            carrito.add(entrada)
        }
        mensajeOperacion.value = "Entrada agregada al carrito"
    }

    private fun generarCodigoQR(entradaId: String): String {
        val letras = ('A'..'Z').random()
        val numeros = Random.nextInt(100, 999)
        return "QR-EMPRESAX-$entradaId-$letras$numeros"
    }

    fun comprar() {
        viewModelScope.launch {
            val email = authDataStore?.email?.firstOrNull() ?: return@launch

            if (carrito.isEmpty()) {
                mensajeOperacion.value = "No hay entradas para comprar"
                return@launch
            }

            val nuevas = carrito.map {
                it.copy(
                    codigoQR = generarCodigoQR(it.id),
                    usuarioEmail = email
                )
            }

            val actualizadas = entradasCompradas + nuevas
            dataStore.saveEntradas(email, actualizadas)
            entradasCompradas.clear()
            entradasCompradas.addAll(actualizadas)

            limpiar()
            mensajeOperacion.value = "Compra realizada con exito"
        }
    }

    fun limpiar() {
        carrito.clear()
        mensajeOperacion.value = "Carrito vacio"
    }

    fun marcarComoUsadaPorCodigo(codigo: String): Boolean {
        val codigoLimpio = codigo.trim().uppercase()
        val entrada = entradasCompradas.find { it.codigoQR?.uppercase() == codigoLimpio } ?: return false

        val actualizadas = entradasCompradas.map {
            if (it.codigoQR?.uppercase() == codigoLimpio)
                it.copy(estado = "ocupada")
            else it
        }

        viewModelScope.launch {
            val email = authDataStore?.email?.firstOrNull() ?: return@launch
            dataStore.saveEntradas(email, actualizadas)
        }

        entradasCompradas.clear()
        entradasCompradas.addAll(actualizadas)
        return true
    }

    fun actualizarUsuario() {
        viewModelScope.launch {
            entradasCompradas.clear()
            recargarEntradasUsuario()
        }
    }
}
