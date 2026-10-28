package com.udv.notasapp

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private const val PREF_NAME = "notas_app_prefs"
    private const val KEY_TOKEN = "jwt_token"
    private const val KEY_NOMBRE = "user_nombre"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun guardarSesion(token: String, nombre: String) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_NOMBRE, nombre)
            .apply()
    }

    fun obtenerToken(): String? {
        return if (::prefs.isInitialized) prefs.getString(KEY_TOKEN, null) else null
    }

    fun obtenerNombre(): String {
        return if (::prefs.isInitialized) prefs.getString(KEY_NOMBRE, "Usuario") ?: "Usuario" else "Usuario"
    }

    fun cerrarSesion() {
        if (::prefs.isInitialized) {
            prefs.edit().clear().apply()
        }
    }
}