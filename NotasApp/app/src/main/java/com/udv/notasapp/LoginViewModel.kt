package com.udv.notasapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val repository = NotasRepository()

    private val _cargando = MutableLiveData<Boolean>()
    val cargando: LiveData<Boolean> get() = _cargando

    private val _loginExitoso = MutableLiveData<LoginResponse?>()
    val loginExitoso: LiveData<LoginResponse?> get() = _loginExitoso

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun iniciarSesion(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _error.value = "Por favor ingrese su correo y contraseña"
            return
        }

        _cargando.value = true
        _error.value = null

        viewModelScope.launch {
            val resultado = repository.login(email.trim(), password.trim())
            _cargando.value = false

            resultado.onSuccess { respuesta ->
                _loginExitoso.value = respuesta
            }.onFailure { excepcion ->
                _error.value = excepcion.message ?: "Error al iniciar sesión"
            }
        }
    }
}