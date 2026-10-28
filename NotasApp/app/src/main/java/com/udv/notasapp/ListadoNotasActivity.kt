package com.udv.notasapp

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListadoNotasActivity : AppCompatActivity() {

    private lateinit var viewModel: ListadoNotasViewModel
    private lateinit var adapter: NotasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_notas)

        viewModel = ViewModelProvider(this)[ListadoNotasViewModel::class.java]

        adapter = NotasAdapter(emptyList()) { notaId ->
            viewModel.eliminarNota(notaId)
        }

        val rvNotas = findViewById<RecyclerView>(R.id.rvNotas)
        val progressBar = findViewById<ProgressBar>(R.id.progressBarListado)

        rvNotas.layoutManager = LinearLayoutManager(this)
        rvNotas.adapter = adapter

        viewModel.cargando.observe(this) { enCarga ->
            progressBar.visibility = if (enCarga) View.VISIBLE else View.GONE
        }

        viewModel.notas.observe(this) { lista ->
            adapter.actualizarNotas(lista)
        }

        viewModel.error.observe(this) { mensajeError ->
            if (!mensajeError.isNullOrEmpty()) {
                Toast.makeText(this, mensajeError, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.cargarNotas()
    }
}