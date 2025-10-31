package com.example.aplicacionpartes;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HistorialActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ParteHistorialAdapter adapter;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        // Inicializar componentes visuales
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Cargar partes desde la base de datos
        dbHelper = new DBHelper(this);
        List<Parte> partes = dbHelper.obtenerPartes();

        // Asignar adaptador específico para historial
        adapter = new ParteHistorialAdapter(partes, this);
        recyclerView.setAdapter(adapter);
    }
}