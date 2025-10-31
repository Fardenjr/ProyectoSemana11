package com.example.aplicacionpartes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ListaEliminableActivity extends AppCompatActivity {

    DBHelper dbHelper;
    RecyclerView recyclerView;
    ParteEliminableAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_eliminable);

        dbHelper = new DBHelper(this);
        recyclerView = findViewById(R.id.recyclerEliminar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Parte> partes = dbHelper.obtenerPartes();
        if (partes.isEmpty()) {
            Toast.makeText(this, "No hay partes registrados", Toast.LENGTH_SHORT).show();
            return;
        }

        adapter = new ParteEliminableAdapter(partes, this);
        recyclerView.setAdapter(adapter);
    }
}