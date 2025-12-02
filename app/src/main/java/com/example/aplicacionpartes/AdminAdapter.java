package com.example.aplicacionpartes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter para mostrar una lista de administradores con opción de selección mediante CheckBox.
 * Se utiliza en la interfaz del usuario normal para elegir a qué administradores enviar mensajes.
 */
public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.AdminViewHolder> {

    // Lista de administradores disponibles
    private final List<Usuario> admins;

    // Lista de UIDs seleccionados por el usuario
    private final List<String> seleccionados;

    /**
     * Constructor del adapter.
     * @param admins Lista de objetos Usuario que representan administradores.
     * @param seleccionados Lista mutable de UIDs seleccionados.
     */
    public AdminAdapter(List<Usuario> admins, List<String> seleccionados) {
        this.admins = admins;
        this.seleccionados = seleccionados;
    }

    /**
     * Infla el layout de cada ítem (item_admin.xml) y crea el ViewHolder.
     */
    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin, parent, false);
        return new AdminViewHolder(view);
    }

    /**
     * Asocia los datos del administrador al ViewHolder.
     * También gestiona la lógica de selección con el CheckBox.
     */
    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {
        Usuario admin = admins.get(position);
        holder.txtAdmin.setText(admin.nombre + " (" + admin.email + ")");

        // Evita que el listener anterior se dispare al hacer setChecked
        holder.checkBox.setOnCheckedChangeListener(null);

        // Marca el CheckBox si el UID está en la lista de seleccionados
        holder.checkBox.setChecked(seleccionados.contains(admin.uid));

        // Listener para agregar o quitar el UID según el estado del CheckBox
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!seleccionados.contains(admin.uid)) seleccionados.add(admin.uid);
            } else {
                seleccionados.remove(admin.uid);
            }
        });
    }

    /**
     * Devuelve la cantidad de administradores en la lista.
     */
    @Override
    public int getItemCount() {
        return admins.size();
    }

    /**
     * ViewHolder que contiene las vistas de cada ítem: nombre/email y CheckBox.
     */
    static class AdminViewHolder extends RecyclerView.ViewHolder {
        TextView txtAdmin;
        CheckBox checkBox;

        public AdminViewHolder(View itemView) {
            super(itemView);
            txtAdmin = itemView.findViewById(R.id.txtAdmin);
            checkBox = itemView.findViewById(R.id.checkBoxAdmin);
        }
    }
}