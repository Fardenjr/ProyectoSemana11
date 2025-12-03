package com.example.aplicacionpartes;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttClientState;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class AdminActivity extends AppCompatActivity {

    private RecyclerView recyclerViewSolicitudes, recyclerViewChat;
    private SolicitudAdapter solicitudAdapter;
    private ChatAdapter chatAdapter;

    private final List<Solicitud> solicitudes = new ArrayList<>();
    private final List<ChatMessage> mensajesChat = new ArrayList<>();

    private EditText inputRespuesta;
    private Button btnResponder;

    private String uidSeleccionado;
    private String uidAdmin;

    private DatabaseReference adminRef;
    private Mqtt5AsyncClient mqttClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin); // usa tu layout con los IDs correctos

        recyclerViewSolicitudes = findViewById(R.id.recyclerSolicitudes);
        recyclerViewChat = findViewById(R.id.recyclerChatAdmin);
        inputRespuesta = findViewById(R.id.inputRespuesta);
        btnResponder = findViewById(R.id.btnResponder);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Debes iniciar sesión como administrador", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        uidAdmin = user.getUid();

        solicitudAdapter = new SolicitudAdapter(solicitudes, this, new SolicitudAdapter.OnSolicitudAccion() {
            @Override
            public void onVerSolicitud(Solicitud solicitud) {
                uidSeleccionado = solicitud.uidUsuario;
                Toast.makeText(AdminActivity.this, "Solicitud de: " + solicitud.nombre, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponder(Solicitud solicitud) {
                uidSeleccionado = solicitud.uidUsuario;
                Toast.makeText(AdminActivity.this, "Preparando respuesta para: " + solicitud.nombre, Toast.LENGTH_SHORT).show();
            }
        });

        chatAdapter = new ChatAdapter(this, mensajesChat, uidAdmin);

        recyclerViewSolicitudes.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSolicitudes.setAdapter(solicitudAdapter);

        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChat.setAdapter(chatAdapter);

        adminRef = FirebaseDatabase.getInstance().getReference("solicitudes_admin");

        suscribirSolicitudes();
        conectarMQTT();

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
            publicarMensajeMQTT(uidSeleccionado, texto);
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
                solicitudAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(AdminActivity.this, "Error cargando solicitudes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enviarRespuesta(String uidUsuario, String textoRespuesta) {
        DatabaseReference respRef = FirebaseDatabase.getInstance()
                .getReference("respuestas_admin")
                .child(uidUsuario);

        RespuestaAdmin respuesta = new RespuestaAdmin(textoRespuesta, System.currentTimeMillis(), uidAdmin);
        respRef.push().setValue(respuesta);
        Toast.makeText(this, "Respuesta guardada en Firebase", Toast.LENGTH_SHORT).show();
    }

    //  MQTT: todo en chat/usuarios
    private void conectarMQTT() {
        mqttClient = MqttClient.builder()
                .useMqttVersion5()
                .identifier("admin-" + UUID.randomUUID())
                .serverHost("broker.hivemq.com")
                .serverPort(1883)
                .buildAsync();

        mqttClient.connectWith().cleanStart(true).send()
                .whenComplete((ack, throwable) -> {
                    if (throwable == null) {
                        mqttClient.subscribeWith()
                                .topicFilter("chat/usuarios")
                                .qos(MqttQos.AT_LEAST_ONCE)
                                .callback(publish -> {
                                    String recibido = new String(publish.getPayloadAsBytes(), StandardCharsets.UTF_8);

                                    runOnUiThread(() -> {
                                        mensajesChat.add(new ChatMessage("usuario", recibido, System.currentTimeMillis()));
                                        chatAdapter.notifyItemInserted(mensajesChat.size() - 1);
                                        recyclerViewChat.scrollToPosition(mensajesChat.size() - 1);
                                    });
                                })
                                .send();
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "Error conectando MQTT", Toast.LENGTH_SHORT).show());
                    }
                });
    }

    private void publicarMensajeMQTT(String uidUsuario, String texto) {
        if (mqttClient == null || mqttClient.getState() != MqttClientState.CONNECTED) {
            Toast.makeText(this, "MQTT no conectado", Toast.LENGTH_SHORT).show();
            return;
        }

        mqttClient.publishWith()
                .topic("chat/usuarios") // único tópico global
                .payload(texto.getBytes(StandardCharsets.UTF_8))
                .qos(MqttQos.AT_LEAST_ONCE)
                .send()
                .whenComplete((ack, throwable) -> {
                    if (throwable == null) {
                        runOnUiThread(() -> {
                            mensajesChat.add(new ChatMessage(uidAdmin, texto, System.currentTimeMillis()));
                            chatAdapter.notifyItemInserted(mensajesChat.size() - 1);
                            recyclerViewChat.scrollToPosition(mensajesChat.size() - 1);
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "Error publicando mensaje MQTT", Toast.LENGTH_SHORT).show());
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mqttClient != null && mqttClient.getState() == MqttClientState.CONNECTED) {
            mqttClient.disconnect();
        }
    }
}