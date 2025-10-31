package com.example.aplicacionpartes;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EliminarParteActivity extends AppCompatActivity {

    DBHelper dbHelper;
    int parteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parte); // Reutilizamos el layout

        dbHelper = new DBHelper(this);
        parteId = getIntent().getIntExtra("parteId", -1);
        if (parteId == -1) return;

        Parte parte = dbHelper.obtenerPartePorId(parteId);
        if (parte == null) return;

        // Mostrar datos como en VistaParteActivity (puedes copiar ese bloque)

        Button btnEliminar = findViewById(R.id.btnGuardar); // Reutilizamos botón
        btnEliminar.setText("Eliminar Parte");

        btnEliminar.setOnClickListener(v -> {
            AutorizacionDialog.mostrar(this, () -> {
                boolean borrado = dbHelper.borrarPartePorId(parteId);
                if (borrado) {
                    Toast.makeText(this, "Parte eliminado correctamente", Toast.LENGTH_SHORT).show();
                    finish(); // Cerrar actividad
                } else {
                    Toast.makeText(this, "Error al eliminar parte", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}