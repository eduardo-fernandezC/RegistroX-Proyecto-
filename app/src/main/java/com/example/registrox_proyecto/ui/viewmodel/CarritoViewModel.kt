package com.example.registrox_proyecto.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.registrox_proyecto.data.datastore.EntradasDataStore
import com.example.registrox_proyecto.data.datastore.AuthDataStore
import com.example.registrox_proyecto.data.model.*
import com.example.registrox_proyecto.data.remote.RetrofitInstance
import com.example.registrox_proyecto.data.repository.ComprasRepository
import com.example.registrox_proyecto.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CarritoViewModel(
    application: Application,
    private val dataStore: EntradasDataStore,
    private val authDataStore: AuthDataStore? = null
) : AndroidViewModel(application) {

    val carrito = mutableStateListOf<Entrada>()
    val ticketsUsuario = mutableStateListOf<CompraEntrada>()
    val mensajeOperacion = mutableStateOf("")
    val isLoading = mutableStateOf(false)

    private fun context(): Context = getApplication<Application>().applicationContext

    fun agregar(entrada: Entrada) {
        val existente = carrito.find { it.id == entrada.id }
        if (existente != null) {
            val actualizado = existente.copy(cantidad = existente.cantidad + entrada.cantidad)
            carrito.remove(existente)
            carrito.add(actualizado)
        } else carrito.add(entrada)
        mensajeOperacion.value = "Entrada agregada al carrito"
    }

    private fun generarCodigoQRUnico(entradaId: Long?, email: String): String {
        val timestamp = System.currentTimeMillis()
        return "QR-${email.hashCode()}-${entradaId ?: 0}-$timestamp"
    }

    fun comprar() {
        viewModelScope.launch {
            if (!NetworkUtils.isNetworkAvailable(context())) {
                mensajeOperacion.value = "Sin conexion a internet"
                return@launch
            }

            val email = authDataStore?.email?.firstOrNull() ?: run {
                mensajeOperacion.value = "Error: usuario no encontrado"
                return@launch
            }

            if (carrito.isEmpty()) {
                mensajeOperacion.value = "No hay entradas en el carrito"
                return@launch
            }

            try {
                val usuarioResponse = withContext(Dispatchers.IO) {
                    RetrofitInstance.api.getUsuarios()
                }

                val usuarioExistente = if (usuarioResponse.isSuccessful) {
                    usuarioResponse.body()?.find { it.email.equals(email, ignoreCase = true) }
                } else null

                if (usuarioExistente == null) {
                    mensajeOperacion.value = "Error: usuario no encontrado en el servidor"
                    return@launch
                }

                val tickets = carrito.map { entrada ->
                    CompraEntrada(
                        codigoQR = generarCodigoQRUnico(entrada.id, email),
                        estado = "disponible",
                        entrada = entrada
                    )
                }

                val compra = Compra(usuario = usuarioExistente, compraEntradas = tickets)

                val response = withContext(Dispatchers.IO) {
                    RetrofitInstance.apiCompras.createCompra(compra)
                }

                if (response.isSuccessful) {
                    mensajeOperacion.value = "Compra registrada correctamente"
                    limpiar()
                    sincronizarEntradasDesdeApi(email)
                } else {
                    mensajeOperacion.value = "Error al registrar compra (${response.code()})"
                }
            } catch (e: Exception) {
                mensajeOperacion.value = "Error de red: ${e.localizedMessage}"
            }
        }
    }

    private suspend fun sincronizarEntradasDesdeApi(email: String) {
        if (!NetworkUtils.isNetworkAvailable(context())) {
            mensajeOperacion.value = "Sin conexión a internet"
            return
        }
        try {
            val repo = ComprasRepository()
            val response = withContext(Dispatchers.IO) { repo.obtenerCompras() }

            if (response.isSuccessful) {
                val todas = response.body() ?: emptyList()
                val comprasUsuario = todas.filter { it.usuario.email.equals(email, true) }
                val ticketsUsuarioActual = comprasUsuario.flatMap { it.compraEntradas ?: emptyList() }

                dataStore.saveEntradas(email, ticketsUsuarioActual.mapNotNull { it.entrada })
                ticketsUsuario.clear()
                ticketsUsuario.addAll(ticketsUsuarioActual)
                mensajeOperacion.value = "Entradas sincronizadas correctamente"
            } else {
                mensajeOperacion.value = "Error al sincronizar (${response.code()})"
            }
        } catch (e: Exception) {
            mensajeOperacion.value = "Error de red: ${e.localizedMessage}"
        }
    }

    fun limpiar() {
        carrito.clear()
        mensajeOperacion.value = "Carrito vacio"
    }

    fun actualizarUsuario() {
        viewModelScope.launch {
            if (!NetworkUtils.isNetworkAvailable(context())) {
                mensajeOperacion.value = "Sin conexion a internet"
                return@launch
            }

            isLoading.value = true
            try {
                ticketsUsuario.clear()
                val email = authDataStore?.email?.firstOrNull()?.trim().orEmpty()
                if (email.isBlank()) {
                    dataStore.clearEntradas("anonimo")
                    return@launch
                }
                sincronizarEntradasDesdeApi(email)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun marcarEntradaUsadaRemota(codigoQR: String) {
        viewModelScope.launch {
            if (!NetworkUtils.isNetworkAvailable(context())) {
                mensajeOperacion.value = "Sin conexion a internet"
                return@launch
            }

            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitInstance.apiEntradas.marcarEntradaUsada(codigoQR)
                }
                if (response.isSuccessful) {
                    val codigoLimpio = codigoQR.trim().uppercase()
                    val actualizados = ticketsUsuario.map {
                        if (it.codigoQR?.uppercase() == codigoLimpio)
                            it.copy(estado = "ocupada")
                        else it
                    }
                    ticketsUsuario.clear()
                    ticketsUsuario.addAll(actualizados)
                    mensajeOperacion.value = response.body()?.get("mensaje") ?: "Entrada validada correctamente"
                } else {
                    mensajeOperacion.value = response.errorBody()?.string()
                        ?: "Error al marcar la entrada como usada"
                }
            } catch (e: Exception) {
                mensajeOperacion.value = "Error de red: ${e.localizedMessage}"
            }
        }
    }

    fun eliminarOcupadas() {
        viewModelScope.launch {
            val disponibles = ticketsUsuario.filter { it.estado.lowercase() != "ocupada" }
            ticketsUsuario.clear()
            ticketsUsuario.addAll(disponibles)
            mensajeOperacion.value = "Entradas ocupadas eliminadas"
        }
    }
}
