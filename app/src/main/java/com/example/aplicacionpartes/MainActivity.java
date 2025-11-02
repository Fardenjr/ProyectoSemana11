package com.example.aplicacionpartes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    Button btnHacerParte, btnEditarParte, btnHistorial, btnEliminarParte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        // Ajuste visual para barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Enlazar botones
        btnHacerParte = findViewById(R.id.btnHacerParte);
        btnEditarParte = findViewById(R.id.btnEditarParte);
        btnHistorial = findViewById(R.id.btnHistorial);
        btnEliminarParte = findViewById(R.id.btnEliminarParte); // Nuevo botón

        // Navegar a ParteActivity (crear nuevo parte)
        btnHacerParte.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ParteActivity.class);
            startActivity(intent);
        });

        // Navegar a ListaEditableActivity (editar partes existentes)
        btnEditarParte.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ListaEditableActivity.class);
            startActivity(intent);
        });

        // Navegar a HistorialActivity (ver partes en PDF)
        btnHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistorialActivity.class);
            startActivity(intent);
        });

        // Navegar a ListaEliminableActivity (eliminar partes con autorización)
        btnEliminarParte.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ListaEliminableActivity.class);
            startActivity(intent);
        });
    }
}