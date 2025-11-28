package com.example.aplicacionpartes;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SolicitudAdapter adapter;
    private List<Solicitud> solicitudes;

    private DatabaseReference adminRef;

    private EditText inputRespuesta;
    private Button btnResponder;
    private String uidSeleccionado; // UID del usuario seleccionado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Inicializar RecyclerView y Adapter
        recyclerView = findViewById(R.id.recyclerSolicitudes);
        solicitudes = new ArrayList<>();
        adapter = new SolicitudAdapter(solicitudes, this, uid -> {
            // Guardamos el UID del usuario al que queremos responder
            uidSeleccionado = uid;
            Toast.makeText(this, "Usuario seleccionado: " + uid, Toast.LENGTH_SHORT).show();
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Inicializar campo de respuesta y botón
        inputRespuesta = findViewById(R.id.inputRespuesta);
        btnResponder = findViewById(R.id.btnResponder);

        // Referencia a solicitudes en Firebase
        adminRef = FirebaseDatabase.getInstance().getReference("solicitudes_admin");

        // Suscribir a cambios en solicitudes
        suscribirSolicitudes();

        // Acción del botón Responder
        btnResponder.setOnClickListener(v -> {
            if (uidSeleccionado == null) {
                Toast.makeText(this, "Selecciona primero un usuario", Toast.LENGTH_SHORT).show();
                return;
            }
            String texto = inputRespuesta.getText().toString().trim();
            if (texto.isEmpty()) {
                Toast.makeText(this, "Escribe una respuesta antes de enviar", Toast.LENGTH_SHORT).show();
                return;
            }

            enviarRespuesta(uidSeleccionado, texto);
            inputRespuesta.setText("");
        });
    }

    private void suscribirSolicitudes() {
        adminRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                solicitudes.clear();
                for (DataSnapshot usuarioSnap : snapshot.getChildren()) {
                    String uid = usuarioSnap.getKey();
                    for (DataSnapshot solicitudSnap : usuarioSnap.getChildren()) {
                        Solicitud s = solicitudSnap.getValue(Solicitud.class);
                        if (s != null) {
                            s.uidUsuario = uid;
                            solicitudes.add(s);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(AdminActivity.this, "Error cargando solicitudes: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enviarRespuesta(String uidUsuario, String textoRespuesta) {
        DatabaseReference respRef = FirebaseDatabase.getInstance()
                .getReference("respuestas_admin")
                .child(uidUsuario);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", textoRespuesta);
        respuesta.put("timestamp", System.currentTimeMillis());
        respuesta.put("autor", "admin");

        respRef.push().setValue(respuesta);
        Toast.makeText(this, "Respuesta enviada al usuario " + uidUsuario, Toast.LENGTH_SHORT).show();
    }
}