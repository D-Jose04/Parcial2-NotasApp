package com.udv.notasapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotasAdapter(
    private var listaNotas: List<Nota> = emptyList(),
    private val onEliminarClick: (Int) -> Unit
) : RecyclerView.Adapter<NotasAdapter.NotaViewHolder>() {

    class NotaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitulo: TextView = itemView.findViewById(R.id.tvItemTitulo)
        val tvContenido: TextView = itemView.findViewById(R.id.tvItemContenido)
        val btnEliminar: Button = itemView.findViewById(R.id.btnEliminarNota)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotaViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_nota, parent, false)
        return NotaViewHolder(vista)
    }

    override fun onBindViewHolder(holder: NotaViewHolder, position: Int) {
        val nota = listaNotas[position]
        holder.tvTitulo.text = nota.titulo
        holder.tvContenido.text = nota.contenido

        holder.btnEliminar.setOnClickListener {
            onEliminarClick(nota.id)
        }
    }

    override fun getItemCount(): Int = listaNotas.size

    fun actualizarNotas(nuevasNotas: List<Nota>) {
        listaNotas = nuevasNotas
        notifyDataSetChanged()
    }
}