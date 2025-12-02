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
        setContentView(R.layout.activity_parte); // Reutilizamos el layout existente

        firebaseHelper = new FirebaseHelper();
        parteId = getIntent().getStringExtra("parteId");

        // Validar que se recibió un ID
        if (parteId == null) {
            Toast.makeText(this, "ID de parte no recibido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Obtener el parte desde Firebase
        firebaseHelper.obtenerPartePorId(parteId, new FirebaseHelper.OnParteListener() {
            @Override
            public void onParteObtenido(Parte parte) {
                if (parte == null) {
                    Toast.makeText(EliminarParteActivity.this, "Parte no encontrado", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                // Reutilizamos el botón existente en el layout
                Button btnEliminar = findViewById(R.id.btnGuardar);
                btnEliminar.setText("Eliminar Parte");

                btnEliminar.setOnClickListener(v -> {
                    // Mostrar diálogo de autorización antes de eliminar
                    AutorizacionDialog.mostrar(EliminarParteActivity.this, () -> {
                        firebaseHelper.borrarPartePorId(parteId, new FirebaseHelper.OnResultadoListener() {
                            @Override
                            public void onExito() {
                                Toast.makeText(EliminarParteActivity.this, "Parte eliminado correctamente", Toast.LENGTH_SHORT).show();
                                finish(); // cerrar actividad
                            }

                            @Override
                            public void onError(Exception e) {
                                Toast.makeText(EliminarParteActivity.this, "Error al eliminar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
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