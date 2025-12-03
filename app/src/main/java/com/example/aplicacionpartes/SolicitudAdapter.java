package com.example.aplicacionpartes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SolicitudAdapter extends RecyclerView.Adapter<SolicitudAdapter.ViewHolder> {

    // Interfaz para manejar acciones en cada solicitud
    public interface OnSolicitudAccion {
        void onVerSolicitud(Solicitud solicitud);
        void onResponder(Solicitud solicitud);
    }

    private final List<Solicitud> lista;
    private final Context context;
    private final OnSolicitudAccion listener;

    public SolicitudAdapter(List<Solicitud> lista, Context context, OnSolicitudAccion listener) {
        this.lista = lista;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_solicitud, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Solicitud s = lista.get(position);

        // Nombre completo
        String nombreCompleto = (s.nombre != null ? s.nombre : "") + " " +
                (s.apellido != null ? s.apellido : "");
        h.txtUsuario.setText(nombreCompleto.trim().isEmpty() ? "Usuario" : nombreCompleto.trim());

        // UID o correo
        h.txtEmail.setText(s.uidUsuario != null ? s.uidUsuario : "UID no disponible");

        // Motivo de la solicitud
        h.txtMotivo.setText(s.mensaje != null ? s.mensaje : "Motivo no especificado");

        // Acciones de botones
        h.btnVer.setOnClickListener(v -> listener.onVerSolicitud(s));
        h.btnResponder.setOnClickListener(v -> listener.onResponder(s));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    // ViewHolder para cada item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtUsuario, txtEmail, txtMotivo;
        Button btnVer, btnResponder;

        public ViewHolder(View itemView) {
            super(itemView);
            txtUsuario = itemView.findViewById(R.id.txtUsuario);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            txtMotivo = itemView.findViewById(R.id.txtMotivo);
            btnVer = itemView.findViewById(R.id.btnVerSolicitud);
            btnResponder = itemView.findViewById(R.id.btnResponder);
        }
    }
}