package com.example.aplicacionpartes;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ParteHistorialAdapter extends RecyclerView.Adapter<ParteHistorialAdapter.ParteViewHolder> {

    private final List<Parte> lista;
    private final Context context;

    public ParteHistorialAdapter(List<Parte> lista, Context context) {
        this.lista = lista;
        this.context = context;
    }

    @NonNull
    @Override
    public ParteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_parte_historial, parent, false);
        return new ParteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParteViewHolder holder, int position) {
        Parte parte = lista.get(position);

        // Mostrar nombre y patente
        holder.txtNombre.setText(parte.nombres);
        holder.txtPatente.setText("Patente: " + parte.patente);

        // Mostrar imagen si existe
        if (parte.foto_path != null && !parte.foto_path.isEmpty()) {
            holder.imgFoto.setImageURI(Uri.parse(parte.foto_path));
        } else {
            // Imagen por defecto si no hay foto (opcional)
            holder.imgFoto.setImageResource(android.R.drawable.ic_menu_report_image);
        }

        // Botón para ver parte en modo lectura
        holder.btnVer.setOnClickListener(v -> {
            Intent intent = new Intent(context, VistaParteActivity.class);
            intent.putExtra("parteId", parte.id);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ParteViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFoto;
        TextView txtNombre, txtPatente;
        Button btnVer;

        public ParteViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFoto = itemView.findViewById(R.id.imgFotoHistorial);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtPatente = itemView.findViewById(R.id.txtPatente);
            btnVer = itemView.findViewById(R.id.btnVerParte);
        }
    }
}