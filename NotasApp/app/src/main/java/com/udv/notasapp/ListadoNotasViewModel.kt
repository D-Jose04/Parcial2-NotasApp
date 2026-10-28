package com.udv.notasapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ListadoNotasViewModel : ViewModel() {

    private val repository = NotasRepository()

    private val _cargando = MutableLiveData<Boolean>()
    val cargando: LiveData<Boolean> get() = _cargando

    private val _notas = MutableLiveData<List<Nota>>()
    val notas: LiveData<List<Nota>> get() = _notas

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun cargarNotas() {
        _cargando.value = true
        _error.value = null

        viewModelScope.launch {
            val resultado = repository.obtenerNotas()
            _cargando.value = false

            resultado.onSuccess { lista ->
                _notas.value = lista
            }.onFailure { excepcion ->
                _error.value = excepcion.message ?: "Error al obtener las notas"
            }
        }
    }

    fun eliminarNota(id: Int) {
        viewModelScope.launch {
            val resultado = repository.eliminarNota(id)
            resultado.onSuccess {
                cargarNotas()
            }.onFailure { excepcion ->
                _error.value = excepcion.message ?: "No se pudo eliminar la nota"
            }
        }
    }
}