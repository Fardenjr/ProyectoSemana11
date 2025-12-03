package com.example.aplicacionpartes;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class VerSolicitudActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_solicitud);

        TextView txtDatos = findViewById(R.id.txtDatosSolicitud);

        Solicitud s = (Solicitud) getIntent().getSerializableExtra("solicitud");

        if (s != null) {
            String detalle =
                    "Nombre: " + safe(s.nombre) + "\n" +
                            "Apellido: " + safe(s.apellido) + "\n" +
                            "Cargo: " + safe(s.cargo) + "\n" +
                            "Institución: " + safe(s.institucion) + "\n" +
                            "Mensaje: " + safe(s.mensaje) + "\n" +
                            "UID: " + safe(s.uidUsuario);

            txtDatos.setText(detalle);
        } else {
            txtDatos.setText("Solicitud no disponible.");
        }
    }

    private String safe(String s) {
        return s == null || s.trim().isEmpty() ? "No disponible" : s;
    }
}