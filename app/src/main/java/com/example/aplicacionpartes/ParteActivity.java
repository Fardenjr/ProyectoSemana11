package com.example.aplicacionpartes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;

public class ParteActivity extends AppCompatActivity {

    // Campos del formulario
    EditText nombres, apellidos, rut, edad, modelo, patente;
    EditText nombreFuncionario, numeroPlaca, lugarPago, autorId; // ✅ autorId agregado
    Spinner tipoAuto, marca, causal, departamento;
    RadioGroup grupoGravedad;
    RadioButton rbGravisima, rbGrave, rbLeve;
    ImageView fotoVehiculo;
    Button btnGuardar, btnFoto;

    // Ruta de la foto tomada
    String fotoPath = "";

    // Firebase
    FirebaseHelper firebaseHelper;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parte);

        // Enlazar vistas
        nombres = findViewById(R.id.nombres);
        apellidos = findViewById(R.id.apellidos);
        rut = findViewById(R.id.rut);
        edad = findViewById(R.id.edad);
        modelo = findViewById(R.id.modelo);
        patente = findViewById(R.id.patente);
        tipoAuto = findViewById(R.id.tipoAuto);
        marca = findViewById(R.id.marca);
        causal = findViewById(R.id.causal);
        departamento = findViewById(R.id.departamento);
        nombreFuncionario = findViewById(R.id.nombreFuncionario);
        numeroPlaca = findViewById(R.id.numeroPlaca);
        lugarPago = findViewById(R.id.lugarPago);
        autorId = findViewById(R.id.autorId); // ✅ Enlace agregado
        grupoGravedad = findViewById(R.id.grupoGravedad);
        rbGravisima = findViewById(R.id.rbGravisima);
        rbGrave = findViewById(R.id.rbGrave);
        rbLeve = findViewById(R.id.rbLeve);
        fotoVehiculo = findViewById(R.id.fotoVehiculo);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnFoto = findViewById(R.id.btnFoto);

        firebaseHelper = new FirebaseHelper();

        cargarSpinners();

        btnFoto.setOnClickListener(v -> abrirCamara());
        btnGuardar.setOnClickListener(v -> guardarParte());
    }

    private void cargarSpinners() {
        tipoAuto.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"SUV", "Sedán", "Camioneta", "Hatchback"}));

        marca.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Toyota", "Hyundai", "Chevrolet", "Kia", "Nissan", "Volkswagen", "Ford", "Honda"}));

        departamento.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Carabinero", "Dirección de Tránsito"}));

        grupoGravedad.setOnCheckedChangeListener((group, checkedId) -> {
            String[] causalesFiltradas;
            if (checkedId == R.id.rbGravisima) {
                causalesFiltradas = new String[]{
                        "Conducir bajo los efectos del alcohol o drogas",
                        "Participar en carreras clandestinas (piques)",
                        "No respetar luz roja del semáforo",
                        "Conducir sin licencia o con licencia suspendida",
                        "No detenerse ante cruce ferroviario",
                        "Conducir en sentido contrario",
                        "No respetar señal de 'Pare' o 'Ceda el paso'",
                        "No detenerse ante un vehículo escolar detenido",
                        "Conducir a exceso de velocidad sobre 60 km/h del límite permitido"
                };
            } else if (checkedId == R.id.rbGrave) {
                causalesFiltradas = new String[]{
                        "No respetar señal de tránsito reglamentaria",
                        "Conducir sin portar licencia vigente",
                        "Vehículo sin placa patente o con patente ilegible",
                        "Exceso de velocidad entre 20 y 60 km/h sobre el límite",
                        "Circular por vías exclusivas de transporte público",
                        "Desobedecer instrucciones de Carabineros",
                        "No usar cinturón de seguridad",
                        "Transportar niños sin sistema de retención infantil",
                        "No mantener distancia razonable con otro vehículo",
                        "No respetar paso de peatones",
                        "Virar en lugar prohibido",
                        "No portar elementos obligatorios (extintor, triángulo, chaleco reflectante)"
                };
            } else {
                causalesFiltradas = new String[]{
                        "Estacionar en doble fila",
                        "No bajar luces en carretera",
                        "Circular con neumáticos en mal estado",
                        "No señalizar antes de virar o cambiar de pista",
                        "Estacionar a menos de 10 metros de una esquina",
                        "Usar bocina innecesariamente",
                        "No portar licencia de conducir (pero tenerla vigente)",
                        "Estacionar frente a grifos o salidas de vehículos",
                        "Circular con vidrios polarizados sin autorización",
                        "No mantener el vehículo limpio o con patente visible",
                        "No respetar límites de velocidad menores (hasta 10 km/h sobre lo permitido)"
                };
            }

            causal.setAdapter(new ArrayAdapter<>(ParteActivity.this,
                    android.R.layout.simple_spinner_dropdown_item, causalesFiltradas));
        });

        rbGrave.setChecked(true);
    }

    private void abrirCamara() {
        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 100);
        } else {
            lanzarIntentCamara();
        }
    }

    private void lanzarIntentCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File foto = crearArchivoImagen();
            Uri fotoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", foto);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private File crearArchivoImagen() {
        String nombreArchivo = "FOTO_" + System.currentTimeMillis();
        File directorio = getFilesDir();
        File imagen = new File(directorio, nombreArchivo + ".jpg");
        fotoPath = imagen.getAbsolutePath();
        return imagen;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            fotoVehiculo.setImageURI(Uri.fromFile(new File(fotoPath)));
        }
    }

    private void guardarParte() {
        Parte parte = new Parte();

        parte.nombres = nombres.getText().toString();
        parte.apellidos = apellidos.getText().toString();
        parte.rut = rut.getText().toString();
        parte.edad = Integer.parseInt(edad.getText().toString());
        parte.tipo_auto = tipoAuto.getSelectedItem().toString();
        parte.marca = marca.getSelectedItem().toString();
        parte.modelo = modelo.getText().toString();
        parte.patente = patente.getText().toString();
        parte.causal = causal.getSelectedItem().toString();
        parte.foto_path = fotoPath;
        parte.nombre_funcionario = nombreFuncionario.getText().toString();
        parte.numero_placa = numeroPlaca.getText().toString();
        parte.departamento = departamento.getSelectedItem().toString();
        parte.lugar_pago = lugarPago.getText().toString();

        int gravedadId = grupoGravedad.getCheckedRadioButtonId();
        if (gravedadId == R.id.rbGravisima) parte.gravedad = "Gravísima";
        else if (gravedadId == R.id.rbGrave) parte.gravedad = "Grave";
        else if (gravedadId == R.id.rbLeve) parte.gravedad = "Leve";

        parte.autorId = autorId.getText().toString().trim(); // ✅ Captura manual del autor

        firebaseHelper.insertarParte(parte, new FirebaseHelper.OnResultadoListener() {
            @Override
            public void onExito() {
                Toast.makeText(ParteActivity.this, "Parte guardado correctamente", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ParteActivity.this, "Error al guardar parte: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}