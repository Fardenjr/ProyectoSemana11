package com.example.aplicacionpartes;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EliminarParteActivity extends AppCompatActivity {

    FirebaseHelper firebaseHelper;
    String parteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parte); // Reutilizamos el layout

        firebaseHelper = new FirebaseHelper();
        parteId = getIntent().getStringExtra("parteId");
        if (parteId == null) {
            Toast.makeText(this, "ID de parte no recibido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        firebaseHelper.obtenerPartePorId(parteId, new FirebaseHelper.OnParteListener() {
            @Override
            public void onParteObtenido(Parte parte) {
                if (parte == null) {
                    Toast.makeText(EliminarParteActivity.this, "Parte no encontrado", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                // Mostrar datos si quieres (como VistaParteActivity)

                Button btnEliminar = findViewById(R.id.btnGuardar); // Reutilizamos botón
                btnEliminar.setText("Eliminar Parte");

                btnEliminar.setOnClickListener(v -> {
                    AutorizacionDialog.mostrar(EliminarParteActivity.this, () -> {
                        firebaseHelper.borrarPartePorId(parteId);
                        Toast.makeText(EliminarParteActivity.this, "Parte eliminado correctamente", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                });
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(EliminarParteActivity.this, "Error al cargar parte", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}