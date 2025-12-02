package com.example.aplicacionpartes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Adaptador para mostrar una lista de partes en un RecyclerView
// Permite editar cada parte mediante un botón que abre EditarParteActivity
public class ParteEditableAdapter extends RecyclerView.Adapter<ParteEditableAdapter.ParteViewHolder> {

    private final List<Parte> lista;   // Lista de partes obtenida desde Firebase
    private final Context context;     // Contexto de la actividad que usa el adaptador

    // Constructor: recibe la lista de partes y el contexto
    public ParteEditableAdapter(List<Parte> lista, Context context) {
        this.lista = lista;
        this.context = context;
    }

    // Crea la vista de cada ítem usando el layout item_parte_editable.xml
    @NonNull
    @Override
    public ParteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_parte_editable, parent, false);
        return new ParteViewHolder(view);
    }

    // Asigna los datos de cada parte a la vista correspondiente
    @Override
    public void onBindViewHolder(@NonNull ParteViewHolder holder, int position) {
        Parte parte = lista.get(position);

        // Mostrar nombre y patente
        holder.txtNombre.setText(parte.nombres);
        holder.txtPatente.setText("Patente: " + parte.patente);

        // Acción del botón Editar: abre EditarParteActivity con el ID del parte
        holder.btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditarParteActivity.class);
            intent.putExtra("parteId", parte.id); // id es String en Firebase
            context.startActivity(intent);
        });
    }

    // Devuelve el número total de elementos en la lista
    @Override
    public int getItemCount() {
        return lista.size();
    }

    // Clase interna ViewHolder: contiene las referencias a las vistas de cada ítem
    public static class ParteViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtPatente;
        Button btnEditar;

        public ParteViewHolder(View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtPatente = itemView.findViewById(R.id.txtPatente);
            btnEditar = itemView.findViewById(R.id.btnEditarParte);
        }
    }
}