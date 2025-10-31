package com.example.aplicacionpartes;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AutorizacionDialog {

    public interface AutorizacionCallback {
        void onAutorizado();
    }

    public static void mostrar(Context context, AutorizacionCallback callback) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_autorizacion, null);
        EditText etRol = dialogView.findViewById(R.id.etRol);
        EditText etClave = dialogView.findViewById(R.id.etClave);

        new AlertDialog.Builder(context)
                .setTitle("Autorización requerida")
                .setView(dialogView)
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    String rol = etRol.getText().toString().trim().toLowerCase();
                    String clave = etClave.getText().toString().trim();

                    if ((rol.equals("sargento") || rol.equals("dirección")) && clave.equals("1234")) {
                        callback.onAutorizado();
                    } else {
                        Toast.makeText(context, "Autorización inválida", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}