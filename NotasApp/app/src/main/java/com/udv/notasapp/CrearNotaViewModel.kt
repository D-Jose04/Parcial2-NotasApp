package com.udv.notasapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class CrearNotaViewModel : ViewModel() {

    private val repository = NotasRepository()

    private val _cargando = MutableLiveData<Boolean>()
    val cargando: LiveData<Boolean> get() = _cargando

    private val _notaCreada = MutableLiveData<Nota?>()
    val notaCreada: LiveData<Nota?> get() = _notaCreada

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun guardarNota(titulo: String, contenido: String) {
        if (titulo.isBlank() || contenido.isBlank()) {
            _error.value = "El título y el contenido son obligatorios"
            return
        }

        _cargando.value = true
        _error.value = null

        viewModelScope.launch {
            val resultado = repository.crearNota(titulo.trim(), contenido.trim())
            _cargando.value = false

            resultado.onSuccess { nota ->
                _notaCreada.value = nota
            }.onFailure { excepcion ->
                _error.value = excepcion.message ?: "Error al guardar la nota"
            }
        }
    }
}