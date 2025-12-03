package com.example.aplicacionpartes;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistorialActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ParteHistorialAdapter adapter;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseHelper = new FirebaseHelper();

        firebaseHelper.obtenerTodosLosPartes(new FirebaseHelper.OnListaPartesListener() {
            @Override
            public void onPartesObtenidos(List<Parte> partes) {
                adapter = new ParteHistorialAdapter(partes, HistorialActivity.this);
                recyclerView.setAdapter(adapter);
                if (partes == null || partes.isEmpty()) {
                    Toast.makeText(HistorialActivity.this, "No hay partes registrados.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(HistorialActivity.this, "Error al cargar historial: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}