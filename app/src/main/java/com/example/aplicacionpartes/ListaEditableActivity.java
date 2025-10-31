package com.example.aplicacionpartes;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ListaEditableActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ParteEditableAdapter adapter;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_editable);

        // Enlazar RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Cargar datos desde la base
        dbHelper = new DBHelper(this);
        List<Parte> partes = dbHelper.obtenerPartes();

        // Asignar adaptador para edición
        adapter = new ParteEditableAdapter(partes, this);
        recyclerView.setAdapter(adapter);
    }
}