package com.example.aplicacionpartes;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Actividad que muestra una lista de partes en un RecyclerView
// y permite editarlos mediante el adaptador ParteEditableAdapter
public class ListaEditableActivity extends AppCompatActivity {

    private RecyclerView recyclerView;              // Vista para mostrar la lista
    private ParteEditableAdapter adapter;           // Adaptador para manejar los ítems
    private FirebaseHelper firebaseHelper;          // Helper para interactuar con Firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_editable);

        // Inicializar RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar helper de Firebase
        firebaseHelper = new FirebaseHelper();

        // Usamos el nuevo metodo obtenerPartes para traer todos los partes
        firebaseHelper.obtenerPartes(new FirebaseHelper.OnPartesListener() {
            @Override
            public void onPartesObtenidos(List<Parte> partes) {
                // Cuando se obtienen los partes, se crea el adaptador y se asigna al RecyclerView
                adapter = new ParteEditableAdapter(partes, ListaEditableActivity.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(Exception e) {
                // Si ocurre un error, mostramos un mensaje al usuario
                Toast.makeText(ListaEditableActivity.this,
                        "Error al cargar partes: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}