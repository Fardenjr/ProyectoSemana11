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

public class ParteEditableAdapter extends RecyclerView.Adapter<ParteEditableAdapter.ParteViewHolder> {

    private final List<Parte> lista;
    private final Context context;

    public ParteEditableAdapter(List<Parte> lista, Context context) {
        this.lista = lista;
        this.context = context;
    }

    @NonNull
    @Override
    public ParteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_parte_editable, parent, false);
        return new ParteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParteViewHolder holder, int position) {
        Parte parte = lista.get(position);
        holder.txtNombre.setText(parte.nombres);
        holder.txtPatente.setText("Patente: " + parte.patente);

        holder.btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditarParteActivity.class);
            intent.putExtra("parteId", parte.id); // ahora id es String en Firebase
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

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