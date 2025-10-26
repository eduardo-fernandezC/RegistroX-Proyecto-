package com.example.registrox_proyecto.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.registrox_proyecto.data.model.Entrada
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.entradasDataStore by preferencesDataStore("entradas_prefs")

class EntradasDataStore(private val context: Context) {

    private fun keyForUser(email: String) = stringPreferencesKey("entradas_${email.lowercase()}")

    suspend fun saveEntradas(email: String, entradas: List<Entrada>) {
        val texto = entradas.joinToString(";") {
            "${it.id}|${it.titulo}|${it.precio}|${it.codigoQR}|${it.estado}|${it.usuarioEmail}"
        }
        context.entradasDataStore.edit { prefs ->
            prefs[keyForUser(email)] = texto
        }
    }

    fun entradasPorUsuario(email: String): Flow<List<Entrada>> {
        return context.entradasDataStore.data.map { prefs ->
            val texto = prefs[keyForUser(email)]
            texto?.split(";")?.mapNotNull { item ->
                val campos = item.split("|")
                if (campos.size >= 6) {
                    Entrada(
                        id = campos[0],
                        titulo = campos[1],
                        lugar = "",
                        precio = campos[2].toDoubleOrNull() ?: 0.0,
                        codigoQR = campos[3],
                        estado = campos[4],
                        usuarioEmail = campos[5]
                    )
                } else null
            } ?: emptyList()
        }
    }

    suspend fun clearEntradas(email: String) {
        context.entradasDataStore.edit { prefs ->
            prefs.remove(keyForUser(email))
        }
    }

}
