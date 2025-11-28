package com.example.aplicacionpartes;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    Button btnHacerParte, btnEditarParte, btnHistorial, btnEliminarParte, btnChatBot, btnVerSolicitudes;
    FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Verificar si el usuario está autenticado
        FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioActual == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Configurar la Toolbar
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        // Ajustar márgenes del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar FirebaseHelper
        firebaseHelper = new FirebaseHelper();

        // Enlazar botones
        btnHacerParte = findViewById(R.id.btnHacerParte);
        btnEditarParte = findViewById(R.id.btnEditarParte);
        btnHistorial = findViewById(R.id.btnHistorial);
        btnEliminarParte = findViewById(R.id.btnEliminarParte);
        btnChatBot = findViewById(R.id.btnChatBot);

        // Si agregaste el botón en el layout principal:
        // <Button android:id="@+id/btnVerSolicitudes" ... />
        btnVerSolicitudes = findViewById(R.id.btnVerSolicitudes);
        btnVerSolicitudes.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AdminActivity.class));
        });

        // Navegación
        btnHacerParte.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ParteActivity.class)));
        btnEditarParte.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ListaEditableActivity.class)));
        btnHistorial.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, HistorialActivity.class)));
        btnEliminarParte.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ListaEliminableActivity.class)));
        btnChatBot.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ChatBotActivity.class)));

        if (btnVerSolicitudes != null) {
            btnVerSolicitudes.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AdminActivity.class)));
        }

        //  Mostrar/ocultar botones según rol
        if (firebaseHelper.esAdmin()) {
            // Admin: acceso completo
            btnHacerParte.setVisibility(Button.VISIBLE);
            btnEditarParte.setVisibility(Button.VISIBLE);
            btnHistorial.setVisibility(Button.VISIBLE);
            btnEliminarParte.setVisibility(Button.VISIBLE);
            btnChatBot.setVisibility(Button.VISIBLE);
            if (btnVerSolicitudes != null) btnVerSolicitudes.setVisibility(Button.VISIBLE);
        } else {
            // Usuario normal: acceso limitado
            btnHacerParte.setVisibility(Button.VISIBLE);
            btnHistorial.setVisibility(Button.VISIBLE);
            btnEditarParte.setVisibility(Button.GONE);
            btnEliminarParte.setVisibility(Button.GONE);
            btnChatBot.setVisibility(Button.VISIBLE);
            if (btnVerSolicitudes != null) btnVerSolicitudes.setVisibility(Button.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu); // Archivo en res/menu/main_menu.xml
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_perfil) {
            startActivity(new Intent(MainActivity.this, PerfilActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return true;
        } else if (id == R.id.action_chatbot) {
            startActivity(new Intent(MainActivity.this, ChatBotActivity.class));
            return true;
        } else if (id == R.id.action_ver_solicitudes) {
            // Ahora el admin tiene acceso absoluto
            startActivity(new Intent(MainActivity.this, AdminActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}