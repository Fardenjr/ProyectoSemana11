package com.example.aplicacionpartes;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListaEditableActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ParteEditableAdapter adapter;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_editable);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseHelper = new FirebaseHelper();

        // Usamos el nuevo método obtenerPartes
        firebaseHelper.obtenerPartes(new FirebaseHelper.OnPartesListener() {
            @Override
            public void onPartesObtenidos(List<Parte> partes) {
                adapter = new ParteEditableAdapter(partes, ListaEditableActivity.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ListaEditableActivity.this, "Error al cargar partes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}