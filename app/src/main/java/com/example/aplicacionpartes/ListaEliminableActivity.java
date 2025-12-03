package com.example.aplicacionpartes;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListaEliminableActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ParteEliminableAdapter adapter;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_eliminable);

        recyclerView = findViewById(R.id.recyclerEliminar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseHelper = new FirebaseHelper();

        // ✅ Cargar datos desde Firebase
        firebaseHelper.obtenerPartes(new FirebaseHelper.OnPartesListener() {
            @Override
            public void onPartesObtenidos(List<Parte> partes) {
                if (partes.isEmpty()) {
                    Toast.makeText(ListaEliminableActivity.this, "No hay partes registrados", Toast.LENGTH_SHORT).show();
                    return;
                }

                adapter = new ParteEliminableAdapter(partes, ListaEliminableActivity.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ListaEliminableActivity.this, "Error al cargar partes", Toast.LENGTH_SHORT).show();
            }
        });
    }
}