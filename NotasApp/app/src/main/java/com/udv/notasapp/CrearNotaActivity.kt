package com.udv.notasapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class CrearNotaActivity : AppCompatActivity() {

    private lateinit var viewModel: CrearNotaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_nota)

        viewModel = ViewModelProvider(this)[CrearNotaViewModel::class.java]

        val etTitulo = findViewById<EditText>(R.id.etTituloNota)
        val etContenido = findViewById<EditText>(R.id.etContenidoNota)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarNota)
        val progressBar = findViewById<ProgressBar>(R.id.progressBarCrear)

        btnGuardar.setOnClickListener {
            val titulo = etTitulo.text.toString()
            val contenido = etContenido.text.toString()
            viewModel.guardarNota(titulo, contenido)
        }

        viewModel.cargando.observe(this) { enCarga ->
            progressBar.visibility = if (enCarga) View.VISIBLE else View.GONE
            btnGuardar.isEnabled = !enCarga
        }

        viewModel.notaCreada.observe(this) { nota ->
            if (nota != null) {
                Toast.makeText(this, "Nota guardada exitosamente", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        viewModel.error.observe(this) { mensajeError ->
            if (!mensajeError.isNullOrEmpty()) {
                Toast.makeText(this, mensajeError, Toast.LENGTH_LONG).show()
            }
        }
    }
}