package com.udv.notasapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SessionManager.init(this)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val progressBar = findViewById<ProgressBar>(R.id.progressBarLogin)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            viewModel.iniciarSesion(email, password)
        }

        viewModel.cargando.observe(this) { enCarga ->
            progressBar.visibility = if (enCarga) View.VISIBLE else View.GONE
            btnLogin.isEnabled = !enCarga
        }

        viewModel.loginExitoso.observe(this) { respuesta ->
            if (respuesta != null) {
                SessionManager.guardarSesion(respuesta.token, respuesta.nombre)
                Toast.makeText(this, "Bienvenido ${respuesta.nombre}", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, DashboardActivity::class.java))
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