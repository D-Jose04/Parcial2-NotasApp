package com.udv.notasapp

import com.google.gson.Gson

class NotasRepository {

    private val api = RetrofitClient.apiService
    private val gson = Gson()

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extraerError(response.errorBody()?.string(), "Credenciales incorrectas")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión con el servidor"))
        }
    }

    suspend fun crearNota(titulo: String, contenido: String): Result<Nota> {
        return try {
            val response = api.crearNota(CrearNotaRequest(titulo, contenido))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extraerError(response.errorBody()?.string(), "No se pudo guardar la nota")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión al guardar la nota"))
        }
    }

    suspend fun obtenerNotas(): Result<List<Nota>> {
        return try {
            val response = api.obtenerNotas()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extraerError(response.errorBody()?.string(), "Error al cargar las notas")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión al obtener las notas"))
        }
    }

    private fun extraerError(errorBody: String?, porDefecto: String): String {
        return try {
            if (!errorBody.isNullOrEmpty()) {
                val parsed = gson.fromJson(errorBody, ErrorResponse::class.java)
                parsed.error ?: porDefecto
            } else {
                porDefecto
            }
        } catch (e: Exception) {
            porDefecto
        }
    }
    suspend fun eliminarNota(id: Int): Result<Boolean> {
        return try {
            val response = api.eliminarNota(id)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("No se pudo eliminar la nota"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión al eliminar"))
        }
    }
}