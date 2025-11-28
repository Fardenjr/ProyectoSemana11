package com.example.aplicacionpartes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class SolicitudAdapter extends RecyclerView.Adapter<SolicitudAdapter.ViewHolder> {

    public interface OnSeleccionarListener {
        void onSeleccionarUsuario(String uidUsuario);
    }

    private final List<Solicitud> solicitudes;
    private final Context context;
    private final OnSeleccionarListener listener;

    public SolicitudAdapter(List<Solicitud> solicitudes, Context context, OnSeleccionarListener listener) {
        this.solicitudes = solicitudes;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_solicitud, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Solicitud solicitud = solicitudes.get(position);
        holder.txtMensaje.setText(solicitud.mensaje);

        String hora = DateFormat.getDateTimeInstance().format(new Date(solicitud.timestamp));
        holder.txtHora.setText(hora);

        holder.txtUsuario.setText("UID: " + solicitud.uidUsuario);

        holder.btnSeleccionar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSeleccionarUsuario(solicitud.uidUsuario);
            }
        });
    }

    @Override
    public int getItemCount() {
        return solicitudes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtMensaje, txtHora, txtUsuario;
        Button btnSeleccionar;

        public ViewHolder(View itemView) {
            super(itemView);
            txtMensaje = itemView.findViewById(R.id.txtMensajeSolicitud);
            txtHora = itemView.findViewById(R.id.txtHoraSolicitud);
            txtUsuario = itemView.findViewById(R.id.txtUsuarioSolicitud);
            btnSeleccionar = itemView.findViewById(R.id.btnSeleccionar);
        }
    }
}