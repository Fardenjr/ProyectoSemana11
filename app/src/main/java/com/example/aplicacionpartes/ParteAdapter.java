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

    private final List<Parte> lista;
    private final Context context;
    private final FirebaseHelper firebaseHelper;

    public ParteAdapter(List<Parte> lista, Context context) {
        this.lista = lista;
        this.context = context;
        this.firebaseHelper = new FirebaseHelper();
    }

    @Override
    public ParteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_parte, parent, false);
        return new ParteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ParteViewHolder holder, int position) {
        Parte parte = lista.get(position);

        // Mostrar datos
        holder.txtNombre.setText(parte.nombres + " " + parte.apellidos);
        holder.txtPatente.setText("Patente: " + parte.patente);
        holder.txtCausal.setText("Causal: " + parte.causal);
        holder.txtGravedad.setText("Gravedad: " + parte.gravedad);
        holder.txtAutor.setText("Autor ID: " + parte.autorId);

        // Botón Ver siempre disponible
        holder.btnVer.setOnClickListener(v -> {
            if (parte.id != null) {
                Intent intent = new Intent(context, VistaParteActivity.class);
                intent.putExtra("parteId", parte.id);
                context.startActivity(intent);
            }
        });

        if (firebaseHelper.esAdmin()) {
            // ✅ Admin: acceso completo
            holder.btnEditar.setVisibility(View.VISIBLE);
            holder.btnEliminar.setVisibility(View.VISIBLE);

            // Editar parte
            holder.btnEditar.setOnClickListener(v -> {
                if (parte.id != null) {
                    Intent intent = new Intent(context, EditarParteActivity.class);
                    intent.putExtra("parteId", parte.id);
                    context.startActivity(intent);
                }
            });

            // Eliminar parte
            holder.btnEliminar.setOnClickListener(v -> {
                if (parte.id != null) {
                    firebaseHelper.borrarPartePorId(parte.id);
                    lista.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, lista.size());
                }
            });

        } else {
            // Usuario normal: restringido
            holder.btnEditar.setVisibility(View.GONE);
            holder.btnEliminar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ParteViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtPatente, txtCausal, txtGravedad, txtAutor;
        Button btnVer, btnEditar, btnEliminar;

        public ParteViewHolder(View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtPatente = itemView.findViewById(R.id.txtPatente);
            txtCausal = itemView.findViewById(R.id.txtCausal);
            txtGravedad = itemView.findViewById(R.id.txtGravedad);
            txtAutor = itemView.findViewById(R.id.txtAutorId);
            btnVer = itemView.findViewById(R.id.btnVerParte);
            btnEditar = itemView.findViewById(R.id.btnEditarParte);
            btnEliminar = itemView.findViewById(R.id.btnEliminarParte);
        }
    }
}