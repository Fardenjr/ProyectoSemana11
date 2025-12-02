package com.example.aplicacionpartes;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttClientState;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private final List<ChatMessage> mensajes = new ArrayList<>();

    private EditText inputMensaje;
    private Button btnEnviar;

    private Mqtt5AsyncClient client;
    private String uidSeleccionado;
    private String uidAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recyclerChat);
        inputMensaje = findViewById(R.id.inputMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Debes iniciar sesión como administrador", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        uidAdmin = user.getUid();

        chatAdapter = new ChatAdapter(this, mensajes, uidAdmin);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        client = MqttClient.builder()
                .useMqttVersion5()
                .identifier("admin-" + UUID.randomUUID())
                .serverHost("broker.hivemq.com")
                .serverPort(1883)
                .buildAsync();

        client.connect()
                .whenComplete((ack, error) -> {
                    if (error != null) {
                        Log.e("MQTT", "Error conectando: " + error.getMessage());
                        runOnUiThread(() ->
                                Toast.makeText(this, "Error al conectar MQTT", Toast.LENGTH_SHORT).show());
                    } else {
                        Log.i("MQTT", "Conectado (admin)");
                        suscribirMensajesParaAdmin();
                    }
                });

        btnEnviar.setOnClickListener(v -> {
            String texto = inputMensaje.getText().toString().trim();
            if (texto.isEmpty()) return;
            if (uidSeleccionado == null) {
                Toast.makeText(this, "Selecciona un usuario (se auto-selecciona al recibir mensaje)", Toast.LENGTH_SHORT).show();
                return;
            }
            enviarRespuesta(uidSeleccionado, texto);
            inputMensaje.setText("");
        });
    }

    private void suscribirMensajesParaAdmin() {
        client.subscribeWith()
                .topicFilter("chat/" + uidAdmin + "/+")
                .qos(MqttQos.AT_LEAST_ONCE)
                .callback(publish -> {
                    String payload = new String(publish.getPayloadAsBytes(), StandardCharsets.UTF_8);
                    String topic = publish.getTopic().toString();
                    String[] partes = topic.split("/");
                    String uidRemitente = partes.length > 1 ? partes[1] : "desconocido";
                    uidSeleccionado = uidRemitente;

                    runOnUiThread(() -> {
                        mensajes.add(new ChatMessage(uidRemitente, payload, System.currentTimeMillis()));
                        chatAdapter.notifyItemInserted(mensajes.size() - 1);
                        recyclerView.scrollToPosition(mensajes.size() - 1);
                    });
                })
                .send();
    }

    private void enviarRespuesta(String uidUsuario, String texto) {
        if (client.getState() != MqttClientState.CONNECTED) {
            Toast.makeText(this, "MQTT no conectado", Toast.LENGTH_SHORT).show();
            return;
        }

        String topic = "chat/" + uidUsuario + "/" + uidAdmin;
        client.publishWith()
                .topic(topic)
                .qos(MqttQos.AT_LEAST_ONCE)
                .payload(texto.getBytes(StandardCharsets.UTF_8))
                .send();

        mensajes.add(new ChatMessage(uidAdmin, texto, System.currentTimeMillis()));
        chatAdapter.notifyItemInserted(mensajes.size() - 1);
        recyclerView.scrollToPosition(mensajes.size() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (client != null) client.disconnect();
    }
}