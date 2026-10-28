package com.udv.notasapp

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val nombre: String,
    val email: String
)

data class CrearNotaRequest(
    val titulo: String,
    val contenido: String
)

data class Nota(
    val id: Int,
    val user_id: Int,
    val titulo: String,
    val contenido: String,
    val fecha_creacion: String?
)

data class ErrorResponse(
    val error: String?
)