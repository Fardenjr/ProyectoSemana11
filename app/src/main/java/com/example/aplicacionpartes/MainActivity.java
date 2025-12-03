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

    Button btnHacerParte, btnEditarParte, btnHistorial, btnEliminarParte, btnChatBot, btnVerSolicitudes, btnChat;
    FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioActual == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseHelper = new FirebaseHelper();

        btnHacerParte = findViewById(R.id.btnHacerParte);
        btnEditarParte = findViewById(R.id.btnEditarParte);
        btnHistorial = findViewById(R.id.btnHistorial);
        btnEliminarParte = findViewById(R.id.btnEliminarParte);
        btnChatBot = findViewById(R.id.btnChatBot);
        btnVerSolicitudes = findViewById(R.id.btnVerSolicitudes);
        btnChat = findViewById(R.id.btnChat);

        btnHacerParte.setOnClickListener(v -> startActivity(new Intent(this, ParteActivity.class)));
        btnEditarParte.setOnClickListener(v -> startActivity(new Intent(this, ListaEditableActivity.class)));
        btnHistorial.setOnClickListener(v -> startActivity(new Intent(this, HistorialActivity.class)));
        btnEliminarParte.setOnClickListener(v -> startActivity(new Intent(this, ListaEliminableActivity.class)));
        btnChatBot.setOnClickListener(v -> startActivity(new Intent(this, ChatBotActivity.class)));

        if (btnVerSolicitudes != null) {
            btnVerSolicitudes.setOnClickListener(v -> startActivity(new Intent(this, AdminActivity.class)));
        }

        if (btnChat != null) {
            btnChat.setOnClickListener(v -> {
                if (firebaseHelper.esAdmin()) {
                    startActivity(new Intent(this, AdminActivity.class));
                } else {
                    startActivity(new Intent(this, ChatActivity.class));
                }
            });
        }

        if (firebaseHelper.esAdmin()) {
            btnHacerParte.setVisibility(Button.VISIBLE);
            btnEditarParte.setVisibility(Button.VISIBLE);
            btnHistorial.setVisibility(Button.VISIBLE);
            btnEliminarParte.setVisibility(Button.VISIBLE);
            btnChatBot.setVisibility(Button.VISIBLE);
            if (btnVerSolicitudes != null) btnVerSolicitudes.setVisibility(Button.VISIBLE);
            if (btnChat != null) btnChat.setVisibility(Button.VISIBLE);
        } else {
            btnHacerParte.setVisibility(Button.VISIBLE);
            btnHistorial.setVisibility(Button.VISIBLE);
            btnEditarParte.setVisibility(Button.GONE);
            btnEliminarParte.setVisibility(Button.GONE);
            btnChatBot.setVisibility(Button.VISIBLE);
            if (btnVerSolicitudes != null) btnVerSolicitudes.setVisibility(Button.GONE);
            if (btnChat != null) btnChat.setVisibility(Button.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_perfil) {
            startActivity(new Intent(this, PerfilActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        } else if (id == R.id.action_chatbot) {
            startActivity(new Intent(this, ChatBotActivity.class));
            return true;
        } else if (id == R.id.action_ver_solicitudes) {
            startActivity(new Intent(this, AdminActivity.class));
            return true;
        } else if (id == R.id.action_chat) {
            if (firebaseHelper.esAdmin()) {
                startActivity(new Intent(this, AdminActivity.class));
            } else {
                startActivity(new Intent(this, ChatActivity.class));
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}