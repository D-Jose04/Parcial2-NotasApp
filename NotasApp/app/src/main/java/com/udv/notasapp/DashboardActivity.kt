package com.udv.notasapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)
        val btnIrCrearNota = findViewById<Button>(R.id.btnIrCrearNota)
        val btnIrListadoNotas = findViewById<Button>(R.id.btnIrListadoNotas)

        val nombreUsuario = SessionManager.obtenerNombre()
        tvBienvenida.text = "Bienvenido, $nombreUsuario"

        btnIrCrearNota.setOnClickListener {
            startActivity(Intent(this, CrearNotaActivity::class.java))
        }

        btnIrListadoNotas.setOnClickListener {
            startActivity(Intent(this, ListadoNotasActivity::class.java))
        }
    }
}