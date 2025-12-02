package com.example.aplicacionpartes;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.*;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public class AdminSolicitudesActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private SolicitudAdapter adapter;
    private final List<Solicitud> solicitudes = new ArrayList<>();

    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_solicitudes);

        recycler = findViewById(R.id.recyclerSolicitudes);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SolicitudAdapter(solicitudes, this, new SolicitudAdapter.OnSolicitudAccion() {
            @Override
            public void onVerSolicitud(Solicitud s) {
                mostrarDialogoSolicitud(s);
            }

            @Override
            public void onResponder(Solicitud s) {
                mostrarDialogoRespuesta(s);
            }
        });
        recycler.setAdapter(adapter);

        rootRef = FirebaseDatabase.getInstance().getReference();

        cargarSolicitudes();
    }

    private void cargarSolicitudes() {
        rootRef.child("solicitudes_admin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                solicitudes.clear();
                for (DataSnapshot uidNode : snapshot.getChildren()) {
                    String uidUsuario = uidNode.getKey();
                    for (DataSnapshot sNode : uidNode.getChildren()) {
                        Solicitud s = sNode.getValue(Solicitud.class);
                        if (s != null) {
                            s.id = sNode.getKey();
                            s.uidUsuario = uidUsuario;
                            solicitudes.add(s);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(AdminSolicitudesActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoSolicitud(Solicitud s) {
        String fecha = DateFormat.getDateTimeInstance().format(s.timestamp);
        String detalle =
                "Nombre: " + safe(s.nombre) + "\n" +
                        "Apellido: " + safe(s.apellido) + "\n" +
                        "Cargo: " + safe(s.cargo) + "\n" +
                        "Institución: " + safe(s.institucion) + "\n" +
                        "Mensaje: " + safe(s.mensaje) + "\n" +
                        "Fecha: " + fecha + "\n" +
                        "UID: " + safe(s.uidUsuario);

        new AlertDialog.Builder(this)
                .setTitle("Solicitud")
                .setMessage(detalle)
                .setPositiveButton("Responder", (d, w) -> mostrarDialogoRespuesta(s))
                .setNegativeButton("Cerrar", (d, w) -> d.dismiss())
                .show();
    }

    private void mostrarDialogoRespuesta(Solicitud s) {
        EditText input = (EditText) LayoutInflater.from(this)
                .inflate(R.layout.view_input_respuesta, null);
        input.setHint("Escribe una respuesta para el usuario…");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        new AlertDialog.Builder(this)
                .setTitle("Responder a " + safe(s.nombre))
                .setView(input)
                .setPositiveButton("Enviar", (d, w) -> {
                    String texto = input.getText().toString().trim();
                    if (texto.isEmpty()) {
                        Toast.makeText(this, "La respuesta no puede estar vacía", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    enviarRespuestaAlUsuario(s.uidUsuario, texto);
                })
                .setNegativeButton("Cancelar", (d, w) -> d.dismiss())
                .show();
    }

    private void enviarRespuestaAlUsuario(String uidDestino, String textoRespuesta) {
        DatabaseReference ref = rootRef.child("respuestas_admin").child(uidDestino);
        Mensaje respuesta = new Mensaje(textoRespuesta, true); // autor = "bot"
        ref.push().setValue(respuesta)
                .addOnSuccessListener(a -> Toast.makeText(this, "Respuesta enviada", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private String safe(String s) {
        return s == null || s.trim().isEmpty() ? "No disponible" : s.trim();
    }
}