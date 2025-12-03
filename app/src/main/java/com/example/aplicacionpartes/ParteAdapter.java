package com.example.aplicacionpartes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Adaptador para mostrar partes con opciones de editar y eliminar
public class ParteAdapter extends RecyclerView.Adapter<ParteAdapter.ParteViewHolder> {

    private final List<Parte> lista;
    private final Context context;
    private final FirebaseHelper firebaseHelper;

    public ParteAdapter(List<Parte> lista, Context context) {
        this.lista = lista;
        this.context = context;
        this.firebaseHelper = new FirebaseHelper(); // inicializar helper
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

        // Botón editar
        holder.btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditarParteActivity.class);
            intent.putExtra("parteId", parte.id);
            context.startActivity(intent);
        });

        // Botón eliminar con listener correcto
        holder.btnEliminar.setOnClickListener(v -> {
            if (parte.id != null) {
                firebaseHelper.borrarPartePorId(parte.id, new FirebaseHelper.OnResultadoListener() {
                    @Override
                    public void onExito() {
                        Toast.makeText(context, "Parte eliminado", Toast.LENGTH_SHORT).show();
                        lista.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, lista.size());
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(context, "Error al eliminar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ParteViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtPatente;
        Button btnEditar, btnEliminar;

        public ParteViewHolder(View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtPatente = itemView.findViewById(R.id.txtPatente);
            btnEditar = itemView.findViewById(R.id.btnEditarParte);
            btnEliminar = itemView.findViewById(R.id.btnEliminarParte);
        }
    }
}