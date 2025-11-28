package com.example.aplicacionpartes;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class VistaParteActivity extends AppCompatActivity {

    FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parte_vista);

        firebaseHelper = new FirebaseHelper();
        String parteId = getIntent().getStringExtra("parteId");
        if (parteId == null) {
            Toast.makeText(this, "ID de parte no recibido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView titulo = findViewById(R.id.tituloParte);
        if (titulo != null) titulo.setText("Vista del Parte");

        firebaseHelper.obtenerPartePorId(parteId, new FirebaseHelper.OnParteListener() {
            @Override
            public void onParteObtenido(Parte parte) {
                if (parte == null) {
                    Toast.makeText(VistaParteActivity.this, "Parte no encontrado", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                // Mostrar datos en TextViews
                setText(R.id.txtNombres, "Nombres: " + parte.nombres);
                setText(R.id.txtApellidos, "Apellidos: " + parte.apellidos);
                setText(R.id.txtRut, "RUT: " + parte.rut);
                setText(R.id.txtEdad, "Edad: " + parte.edad);
                setText(R.id.txtTipoAuto, "Tipo de vehículo: " + parte.tipo_auto);
                setText(R.id.txtMarca, "Marca: " + parte.marca);
                setText(R.id.txtModelo, "Modelo: " + parte.modelo);
                setText(R.id.txtPatente, "Patente: " + parte.patente);
                setText(R.id.txtCausal, "Causal: " + parte.causal);
                setText(R.id.txtNombreFuncionario, "Funcionario: " + parte.nombre_funcionario);
                setText(R.id.txtNumeroPlaca, "Número de Placa: " + parte.numero_placa);
                setText(R.id.txtDepartamento, "Departamento: " + parte.departamento);
                setText(R.id.txtLugarPago, "Lugar de pago: " + parte.lugar_pago);

                // Mostrar gravedad en RadioGroup
                RadioGroup grupoGravedad = findViewById(R.id.grupoGravedad);
                if (grupoGravedad != null && parte.gravedad != null) {
                    switch (parte.gravedad) {
                        case "Gravísima": grupoGravedad.check(R.id.rbGravisima); break;
                        case "Grave": grupoGravedad.check(R.id.rbGrave); break;
                        case "Leve": grupoGravedad.check(R.id.rbLeve); break;
                    }
                    for (int i = 0; i < grupoGravedad.getChildCount(); i++) {
                        grupoGravedad.getChildAt(i).setEnabled(false);
                    }
                }

                // Mostrar foto
                ImageView fotoVehiculo = findViewById(R.id.fotoVehiculo);
                if (fotoVehiculo != null) {
                    if (parte.foto_path != null && !parte.foto_path.isEmpty()) {
                        try {
                            fotoVehiculo.setImageURI(Uri.parse(parte.foto_path));
                        } catch (Exception e) {
                            fotoVehiculo.setImageResource(android.R.drawable.ic_menu_report_image);
                        }
                    } else {
                        fotoVehiculo.setImageResource(android.R.drawable.ic_menu_report_image);
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(VistaParteActivity.this, "Error al cargar parte: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void setText(int id, String texto) {
        TextView campo = findViewById(id);
        if (campo != null) campo.setText(texto);
    }
}