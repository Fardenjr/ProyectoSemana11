package com.example.aplicacionpartes;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.*;

public class ChatBotActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatBotAdapter adapter;
    private List<Mensaje> mensajes;
    private EditText inputMensaje;
    private Button btnEnviar;

    private DatabaseReference chatRef;
    private String uid;
    private String conversacionId;

    private Map<String, String> faq;
    private final Handler handler = new Handler();

    // Estado para saber si estamos esperando datos del usuario para contactar admin
    private boolean esperandoDatosAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (uid == null) {
            Toast.makeText(this, "Debes iniciar sesión para usar el chat", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        conversacionId = UUID.randomUUID().toString();
        chatRef = FirebaseDatabase.getInstance().getReference("chatbot_conversaciones")
                .child(uid).child(conversacionId).child("mensajes");

        recyclerView = findViewById(R.id.recyclerChat);
        inputMensaje = findViewById(R.id.inputMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);

        mensajes = new ArrayList<>();
        adapter = new ChatBotAdapter(mensajes, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        inicializarFAQ();
        presentarPartito();
        suscribirRespuestasAdmin();

        btnEnviar.setOnClickListener(v -> {
            String texto = inputMensaje.getText().toString().trim();
            if (TextUtils.isEmpty(texto)) return;

            Mensaje mUsuario = new Mensaje(texto, false);
            pushMensajeFirebase(mUsuario);
            mensajes.add(mUsuario);
            adapter.notifyItemInserted(mensajes.size() - 1);
            recyclerView.scrollToPosition(mensajes.size() - 1);
            inputMensaje.setText("");

            handler.postDelayed(() -> {
                String respuesta = obtenerRespuesta(texto);

                // Mensaje de respuesta individual
                Mensaje mBot = new Mensaje(respuesta, true);
                pushMensajeFirebase(mBot);
                mensajes.add(mBot);
                adapter.notifyItemInserted(mensajes.size() - 1);
                recyclerView.scrollToPosition(mensajes.size() - 1);

                // Luego, en otro mensaje, mostrar el menú
                if (!respuesta.equals(menuOpciones())) {
                    Mensaje mMenu = new Mensaje("¿Tienes alguna otra consulta?\n" + menuOpciones(), true);
                    pushMensajeFirebase(mMenu);
                    mensajes.add(mMenu);
                    adapter.notifyItemInserted(mensajes.size() - 1);
                    recyclerView.scrollToPosition(mensajes.size() - 1);
                }
            }, 1500);
        });
    }

    private void presentarPartito() {
        mensajes.clear();
        adapter.notifyDataSetChanged();

        Mensaje m1 = new Mensaje("Hola, soy Partito 🤖, tu asistente virtual. Estoy aquí para ayudarte en lo que necesites.", true);
        Mensaje m2 = new Mensaje(menuOpciones(), true);

        mensajes.add(m1);
        mensajes.add(m2);
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(mensajes.size() - 1);

        pushMensajeFirebase(m1);
        pushMensajeFirebase(m2);
    }

    private String menuOpciones() {
        return "¿Sobre qué tema quieres ayuda?\n" +
                "1. ¿Cómo hacer un parte?\n" +
                "2. ¿Para qué sirve el historial?\n" +
                "3. ¿Cómo editar un parte?\n" +
                "4. ¿Cómo eliminar un parte?\n" +
                "5. ¿Dónde veo mi perfil?\n" +
                "6. ¿Cómo contactar a mandos superiores?\n" +
                "7. ¿Qué permisos tiene un usuario?\n" +
                "8. ¿Qué permisos tiene un administrador?\n" +
                "9. ¿Cómo iniciar sesión?\n" +
                "10. Contactar Administrador.\n" +
                "11. ¿Cómo cerrar sesión?";
    }

    private void inicializarFAQ() {
        faq = new HashMap<>();
        faq.put("1", "Para hacer un parte, usa la opción 'Hacer Parte' en el menú principal.");
        faq.put("2", "El historial muestra todos los partes creados previamente.");
        faq.put("3", "Solo los administradores pueden editar partes existentes.");
        faq.put("4", "Solo los administradores pueden eliminar partes.");
        faq.put("5", "Tu perfil está en el ícono de la barra superior, opción 'Perfil'.");
        faq.put("6", "Para contactar a mandos superiores, usa el botón 'Chat en Tiempo Real' o escribe a fiscal@ministerio.cl.");
        faq.put("7", "Un usuario puede crear partes y ver su historial, pero no editar ni eliminar.");
        faq.put("8", "Un administrador puede crear, editar, eliminar partes y ver todos los historiales.");
        faq.put("9", "Para iniciar sesión, usa tu correo institucional en la pantalla de login.");
        faq.put("11", "Para cerrar sesión, usa el menú superior y selecciona 'Cerrar sesión'.");
    }

    private String normalizar(String s) {
        s = s.toLowerCase().trim();
        s = s.replace("á","a").replace("é","e").replace("í","i").replace("ó","o").replace("ú","u");
        s = s.replace("?","").replace("¿","").replace(".","").replace(",","");
        return s;
    }

    private String obtenerRespuesta(String preguntaOriginal) {
        String key = normalizar(preguntaOriginal);

        // Opción especial: contactar administrador
        if (key.equals("10") || key.contains("contactar administrador") || key.contains("hablar administrador")) {
            esperandoDatosAdmin = true;
            return "Okey, contactaremos al administrador/superior.\n" +
                    "Primero necesitamos que rellenes estos apartados:\n" +
                    "- Nombre\n- Apellido\n- Cargo\n- Institución a la que perteneces\n- Problema/Solicitud\n\n" +
                    "Por favor escribe toda la información en un solo mensaje.";
        }

        // Si estamos esperando datos del usuario
        if (esperandoDatosAdmin) {
            esperandoDatosAdmin = false;
            guardarSolicitudAdmin(preguntaOriginal);
            return "Gracias, tu solicitud ha sido enviada al administrador.\n" +
                    "Pronto se pondrán en contacto contigo.";
        }

        // Lógica FAQ normal
        String base = null;
        if (faq.containsKey(key)) base = faq.get(key);
        else if (key.contains("hacer") && key.contains("parte")) base = faq.get("1");
        else if (key.contains("historial")) base = faq.get("2");
        else if (key.contains("editar") && key.contains("parte")) base = faq.get("3");
        else if (key.contains("eliminar") && key.contains("parte")) base = faq.get("4");
        else if (key.contains("perfil")) base = faq.get("5");
        else if (key.contains("mandos") || key.contains("fiscal") || key.contains("superiores")) base = faq.get("6");
        else if (key.contains("usuario") && key.contains("permiso")) base = faq.get("7");
        else if (key.contains("admin") || key.contains("administrador")) base = faq.get("8");
        else if (key.contains("iniciar") && key.contains("sesion")) base = faq.get("9");
        else if (key.contains("cerrar") && key.contains("sesion")) base = faq.get("11");

        if (key.equals("menu") || key.equals("0")) {
            return menuOpciones();
        }

        if (base != null) {
            return base;
        }

        return "No tengo esa respuesta aún. Puedes consultar al administrador.";
    }

    private void guardarSolicitudAdmin(String datosUsuario) {
        DatabaseReference adminRef = FirebaseDatabase.getInstance()
                .getReference("solicitudes_admin")
                .child(uid);

        Map<String, Object> solicitud = new HashMap<>();
        solicitud.put("datos", datosUsuario);
        solicitud.put("timestamp", System.currentTimeMillis());

        adminRef.push().setValue(solicitud);
    }

    private void pushMensajeFirebase(Mensaje mensaje) {
        String msgId = chatRef.push().getKey();
        if (msgId != null) {
            chatRef.child(msgId).setValue(mensaje);
        }
    }

    private void suscribirRespuestasAdmin() {
        DatabaseReference respRef = FirebaseDatabase.getInstance()
                .getReference("respuestas_admin")
                .child(uid);

        respRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot s : snapshot.getChildren()) {
                    Mensaje m = s.getValue(Mensaje.class);
                    if (m != null) {
                        mensajes.add(m);
                        adapter.notifyItemInserted(mensajes.size() - 1);
                        recyclerView.scrollToPosition(mensajes.size() - 1);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ChatBotActivity.this,
                        "Error cargando respuestas: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}