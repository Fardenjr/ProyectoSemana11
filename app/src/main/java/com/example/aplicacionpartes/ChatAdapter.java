package com.example.aplicacionpartes;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private final Context context;
    private final List<ChatMessage> mensajes;
    private final String currentUserId;

    public ChatAdapter(Context context, List<ChatMessage> mensajes, String currentUserId) {
        this.context = context;
        this.mensajes = mensajes;
        this.currentUserId = currentUserId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_chat_admin, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int position) {
        ChatMessage m = mensajes.get(position);
        h.txtMensaje.setText(m.texto);
        h.txtRemitente.setText(m.remitente);
        h.txtHora.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date(m.timestamp)));

        boolean esYo = m.remitente.equals(currentUserId);
        h.container.setGravity(esYo ? Gravity.END : Gravity.START);
        h.txtMensaje.setBackgroundResource(esYo ? R.drawable.bg_mensaje_admin : R.drawable.bg_mensaje_usuario);
        h.txtMensaje.setTextColor(ContextCompat.getColor(context, esYo ? android.R.color.white : android.R.color.black));
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtMensaje, txtRemitente, txtHora;
        LinearLayout container;

        public ViewHolder(View itemView) {
            super(itemView);
            txtMensaje = itemView.findViewById(R.id.txtMensaje);
            txtRemitente = itemView.findViewById(R.id.txtRemitente);
            txtHora = itemView.findViewById(R.id.txtHora);
            container = itemView.findViewById(R.id.containerMensaje);
        }
    }
}