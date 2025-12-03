package com.example.aplicacionpartes;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Actividad de chat para usuarios normales.
 * Permite seleccionar un administrador desde un Spinner y enviar mensajes.
 */
public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerChat;
    private ChatAdapter chatAdapter;
    private final List<ChatMessage> mensajes = new ArrayList<>();

    private Spinner spinnerAdmins;
    private EditText inputMensaje;
    private Button btnEnviar;

    // Listas para admins disponibles y sus UIDs
    private final List<Usuario> listaAdmins = new ArrayList<>();
    private final List<String> uidAdmins = new ArrayList<>();

    private Mqtt5AsyncClient mqttClient;
    private String uidRemitente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Inicializar vistas
        inputMensaje = findViewById(R.id.inputMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);
        recyclerChat = findViewById(R.id.recyclerChat);
        spinnerAdmins = findViewById(R.id.spinnerAdmins);

        // Validar sesión
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Debes iniciar sesión primero", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        uidRemitente = user.getUid();

        // Configurar RecyclerView del chat
        chatAdapter = new ChatAdapter(this, mensajes, uidRemitente);
        recyclerChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerChat.setAdapter(chatAdapter);

        // Cargar admins y configurar lógica
        cargarAdmins();
        conectarMQTT();
        escucharMensajesFirebase();

        // Acción de enviar mensaje
        btnEnviar.setOnClickListener(v -> enviarMensaje());
    }

    /**
     * Carga los administradores desde Firebase y los muestra en el Spinner.
     */
    private void cargarAdmins() {
        DatabaseReference refUsuarios = FirebaseDatabase.getInstance().getReference("usuarios");
        refUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                listaAdmins.clear();
                uidAdmins.clear();
                List<String> correos = new ArrayList<>();

                for (DataSnapshot usuarioSnap : snapshot.getChildren()) {
                    Usuario usuario = usuarioSnap.getValue(Usuario.class);
                    if (usuario != null && FirebaseHelper.esCorreoAdmin(usuario.email)) {
                        listaAdmins.add(usuario);
                        uidAdmins.add(usuario.uid);
                        correos.add(usuario.email);
                    }
                }

                // Poblar Spinner con correos de admins
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ChatActivity.this,
                        android.R.layout.simple_spinner_item, correos);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerAdmins.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Error al cargar admins", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Conecta al broker MQTT y se suscribe a mensajes dirigidos al usuario actual.
     */
    private void conectarMQTT() {
        mqttClient = MqttClient.builder()
                .useMqttVersion5()
                .identifier("user-" + UUID.randomUUID())
                .serverHost("broker.hivemq.com")
                .serverPort(1883)
                .buildAsync();

        mqttClient.connect().whenComplete((ack, error) -> {
            if (error != null) {
                Toast.makeText(this, "Error al conectar MQTT", Toast.LENGTH_SHORT).show();
            } else {
                mqttClient.subscribeWith()
                        .topicFilter("chat/" + uidRemitente + "/+")
                        .qos(MqttQos.AT_LEAST_ONCE)
                        .callback(publish -> {
                            String recibido = new String(publish.getPayloadAsBytes(), StandardCharsets.UTF_8);
                            String topic = publish.getTopic().toString();
                            String[] partes = topic.split("/");
                            String remitente = partes.length > 1 ? partes[1] : "admin";

                            runOnUiThread(() -> {
                                mensajes.add(new ChatMessage(remitente, recibido, System.currentTimeMillis()));
                                chatAdapter.notifyItemInserted(mensajes.size() - 1);
                                recyclerChat.scrollToPosition(mensajes.size() - 1);
                            });
                        })
                        .send();
            }
        });
    }

    /**
     * Escucha mensajes guardados en Firebase para el usuario actual.
     */
    private void escucharMensajesFirebase() {
        DatabaseReference refChat = FirebaseDatabase.getInstance()
                .getReference("chat_usuarios")
                .child(uidRemitente);

        refChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                ChatMessage msg = snapshot.getValue(ChatMessage.class);
                if (msg != null) {
                    mensajes.add(msg);
                    chatAdapter.notifyItemInserted(mensajes.size() - 1);
                    recyclerChat.scrollToPosition(mensajes.size() - 1);
                }
            }
            @Override public void onChildChanged(DataSnapshot snapshot, String previousChildName) {}
            @Override public void onChildRemoved(DataSnapshot snapshot) {}
            @Override public void onChildMoved(DataSnapshot snapshot, String previousChildName) {}
            @Override public void onCancelled(DatabaseError error) {}
        });
    }

    /**
     * Envía un mensaje al administrador seleccionado en el Spinner.
     */
    private void enviarMensaje() {
        String texto = inputMensaje.getText().toString().trim();
        if (texto.isEmpty()) return;

        int index = spinnerAdmins.getSelectedItemPosition();
        if (index < 0 || index >= uidAdmins.size()) {
            Toast.makeText(this, "Selecciona un administrador válido", Toast.LENGTH_SHORT).show();
            return;
        }

        String uidDestinatario = uidAdmins.get(index);
        ChatMessage mensaje = new ChatMessage(uidRemitente, texto, System.currentTimeMillis());

        // Guardar en Firebase
        FirebaseDatabase.getInstance()
                .getReference("chat_usuarios")
                .child(uidRemitente)
                .child(uidDestinatario)
                .push()
                .setValue(mensaje);

        // Publicar en MQTT
        mqttClient.publishWith()
                .topic("chat/" + uidDestinatario + "/" + uidRemitente)
                .payload(texto.getBytes(StandardCharsets.UTF_8))
                .qos(MqttQos.AT_LEAST_ONCE)
                .send();

        // Actualizar UI
        mensajes.add(mensaje);
        chatAdapter.notifyItemInserted(mensajes.size() - 1);
        recyclerChat.scrollToPosition(mensajes.size() - 1);
        inputMensaje.setText("");
    }
}