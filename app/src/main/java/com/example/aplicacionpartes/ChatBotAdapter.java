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

public class ChatBotAdapter extends RecyclerView.Adapter<ChatBotAdapter.ViewHolder> {

    private final List<Mensaje> mensajes;
    private final Context context;

    public ChatBotAdapter(List<Mensaje> mensajes, Context context) {
        this.mensajes = mensajes;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mensaje, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Mensaje mensaje = mensajes.get(position);
        holder.txtMensaje.setText(mensaje.texto);

        // Mostrar hora del mensaje
        String hora = DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date(mensaje.timestamp));
        holder.txtHora.setText(hora);

        // Estilo según autor
        if ("bot".equals(mensaje.autor)) {
            holder.txtMensaje.setBackgroundResource(R.drawable.bg_mensaje_bot);
            holder.txtMensaje.setTextColor(ContextCompat.getColor(context, android.R.color.black));
            holder.container.setGravity(Gravity.START);
            holder.labelAutor.setText("Bot");
        } else {
            holder.txtMensaje.setBackgroundResource(R.drawable.bg_mensaje_usuario);
            holder.txtMensaje.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            holder.container.setGravity(Gravity.END);
            holder.labelAutor.setText("Tú");
        }
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtMensaje, txtHora, labelAutor;
        LinearLayout container;

        public ViewHolder(View itemView) {
            super(itemView);
            txtMensaje = itemView.findViewById(R.id.txtMensaje);
            txtHora = itemView.findViewById(R.id.txtHora);
            labelAutor = itemView.findViewById(R.id.labelAutor);
            container = itemView.findViewById(R.id.containerMensaje);
        }
    }
}