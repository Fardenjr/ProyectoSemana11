package com.example.aplicacionpartes;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

// Clase utilitaria para mostrar un diálogo de autorización
public class AutorizacionDialog {

    // Interfaz de callback que se ejecuta si la autorización es válida
    public interface AutorizacionCallback {
        void onAutorizado();
    }

    // Método estático para mostrar el diálogo
    public static void mostrar(Context context, AutorizacionCallback callback) {
        // Inflar el layout personalizado del diálogo
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_autorizacion, null);

        // Obtener referencias a los campos de entrada
        EditText etRol = dialogView.findViewById(R.id.etRol);
        EditText etClave = dialogView.findViewById(R.id.etClave);

        // Construir el AlertDialog
        new AlertDialog.Builder(context)
                .setTitle("Autorización requerida")
                .setView(dialogView)
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    // Obtener valores ingresados
                    String rol = etRol.getText().toString().trim().toLowerCase();
                    String clave = etClave.getText().toString().trim();

                    // Validar rol y clave (puedes extender con más roles o claves)
                    if ((rol.equals("sargento") || rol.equals("dirección")) && clave.equals("1234")) {
                        callback.onAutorizado(); // Ejecutar acción autorizada
                    } else {
                        Toast.makeText(context, "Autorización inválida", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null) // No hacer nada si se cancela
                .show();
    }
}