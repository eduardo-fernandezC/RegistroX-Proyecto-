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
    suspend fun clearEntradas(email: String) {
        context.entradasDataStore.edit { prefs ->
            prefs.remove(keyForUser(email))
        }
    }

}
