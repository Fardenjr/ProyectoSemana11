package com.example.aplicacionpartes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ParteAdapter extends RecyclerView.Adapter<ParteAdapter.ParteViewHolder> {

    private final List<Parte> lista; // Lista de partes a mostrar
    private final Context context;   // Contexto de la actividad que usa el adaptador

    public ParteAdapter(List<Parte> lista, Context context) {
        this.lista = lista;
        this.context = context;
    }

    @Override
    public ParteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflar el layout de cada ítem de la lista
        View view = LayoutInflater.from(context).inflate(R.layout.item_parte, parent, false);
        return new ParteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ParteViewHolder holder, int position) {
        // Obtener el parte actual
        Parte parte = lista.get(position);

        // Mostrar nombre y patente en la tarjeta
        holder.txtNombre.setText(parte.nombres);
        holder.txtPatente.setText("Patente: " + parte.patente);

        // Botón para ver el parte en modo lectura
        holder.btnVer.setOnClickListener(v -> {
            Intent intent = new Intent(context, VistaParteActivity.class);
            intent.putExtra("parteId", parte.id); // Enviar el ID del parte
            context.startActivity(intent);        // Abrir la actividad de visualización
        });
    }

    @Override
    public int getItemCount() {
        return lista.size(); // Cantidad de elementos en la lista
    }

    // Clase interna que representa cada ítem visual del RecyclerView
    public static class ParteViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtPatente;
        Button btnVer;

        public ParteViewHolder(View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtPatente = itemView.findViewById(R.id.txtPatente);
            btnVer = itemView.findViewById(R.id.btnVerParte); // ID del botón en item_parte.xml
        }
    }
}