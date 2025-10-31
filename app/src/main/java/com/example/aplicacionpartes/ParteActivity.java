package com.example.aplicacionpartes;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;

public class ParteActivity extends AppCompatActivity {

    // Campos del formulario
    EditText nombres, apellidos, rut, edad, modelo, patente;
    EditText nombreFuncionario, numeroPlaca, lugarPago;
    Spinner tipoAuto, marca, causal, departamento;
    RadioGroup grupoGravedad;
    RadioButton rbGravisima, rbGrave, rbLeve;
    ImageView fotoVehiculo;
    Button btnGuardar, btnFoto;

    // Ruta de la foto tomada
    String fotoPath = "";

    // Base de datos
    DBHelper dbHelper;

    // Código de solicitud de cámara
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
        grupoGravedad = findViewById(R.id.grupoGravedad);
        rbGravisima = findViewById(R.id.rbGravisima);
        rbGrave = findViewById(R.id.rbGrave);
        rbLeve = findViewById(R.id.rbLeve);
        fotoVehiculo = findViewById(R.id.fotoVehiculo);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnFoto = findViewById(R.id.btnFoto);

        // Inicializar base de datos
        dbHelper = new DBHelper(this);

        // Cargar opciones en los spinners y configurar filtros
        cargarSpinners();

        // Configurar botón para tomar foto
        btnFoto.setOnClickListener(v -> abrirCamara());

        // Configurar botón para guardar parte
        btnGuardar.setOnClickListener(v -> guardarParte());
    }

    // Metodo para cargar opciones en los spinners y configurar el filtro de causales
    private void cargarSpinners() {
        // Tipos de vehículo
        String[] tipos = {"SUV", "Sedán", "Camioneta", "Hatchback"};
        tipoAuto.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tipos));

        // Marcas de vehículo
        String[] marcas = {
                "Acura", "Alfa Romeo", "Aston Martin", "Audi", "Bentley", "BMW", "Bugatti", "Buick",
                "BYD", "Cadillac", "Changan", "Chery", "Chevrolet", "Chrysler", "Citroën", "Cupra",
                "Dacia", "Daewoo", "Daihatsu", "Dodge", "DS Automobiles", "Ferrari", "Fiat", "Ford",
                "Geely", "Genesis", "GMC", "Great Wall", "Haval", "Honda", "Hummer", "Hyundai",
                "Infiniti", "Isuzu", "Iveco", "Jaguar", "Jeep", "Kia", "Koenigsegg", "Lada", "Lamborghini",
                "Lancia", "Land Rover", "Lexus", "Lincoln", "Lotus", "Lucid", "Mahindra", "Maserati",
                "Mazda", "McLaren", "Mercedes-Benz", "MG", "Mini", "Mitsubishi", "Nissan", "Opel",
                "Pagani", "Peugeot", "Polestar", "Pontiac", "Porsche", "RAM", "Renault", "Rivian",
                "Rolls-Royce", "Rover", "Saab", "Seat", "Skoda", "Smart", "SsangYong", "Subaru",
                "Suzuki", "Tata", "Tesla", "Toyota", "Volkswagen", "Volvo", "Zotye"
        };
        marca.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, marcas));

        // Departamentos emisores
        String[] opcionesDepartamento = {"Carabinero", "Dirección de Tránsito"};
        departamento.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, opcionesDepartamento));

        // Configurar filtro de causales según gravedad seleccionada
        grupoGravedad.setOnCheckedChangeListener((group, checkedId) -> {
            String[] causalesFiltradas;

            if (checkedId == R.id.rbGravisima) {
                causalesFiltradas = new String[]{
                        "Participar en carreras clandestinas",
                        "Conducir en condiciones físicas deficientes",
                        "No respetar luz roja del semáforo"
                };
            } else if (checkedId == R.id.rbGrave) {
                causalesFiltradas = new String[]{
                        "No respetar disco Pare", "Conducir sin licencia", "Vehículo sin placa patente",
                        "Exceso de velocidad (20-60 km/h)", "Circular por vías exclusivas de transporte público",
                        "Desobedecer señales de Carabineros"
                };
            } else {
                causalesFiltradas = new String[]{
                        "Estacionar en doble fila", "No bajar luces en carretera", "Circular con neumáticos defectuosos",
                        "No hacer señales antes de virar", "Estacionar a menos de 10 metros de la esquina"
                };
            }

            ArrayAdapter<String> adapterCausal = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, causalesFiltradas);
            causal.setAdapter(adapterCausal);
        });

        // Selección inicial por defecto
        rbGrave.setChecked(true);
    }

    // Metodo para abrir la cámara
    private void abrirCamara() {
        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 100);
        } else {
            lanzarIntentCamara();
        }
    }

    // Metodo para lanzar el intent de captura
    private void lanzarIntentCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File foto = crearArchivoImagen();
            Uri fotoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", foto);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    // Metodo para crear archivo de imagen
    private File crearArchivoImagen() {
        String nombreArchivo = "FOTO_" + System.currentTimeMillis();
        File directorio = getFilesDir();
        File imagen = new File(directorio, nombreArchivo + ".jpg");
        fotoPath = imagen.getAbsolutePath();
        return imagen;
    }

    // Metodo que se ejecuta al volver de la cámara
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            fotoVehiculo.setImageURI(Uri.fromFile(new File(fotoPath)));
        }
    }

    // Metodo para guardar los datos del parte en la base de datos
    private void guardarParte() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        try {
            values.put("nombres", nombres.getText().toString());
            values.put("apellidos", apellidos.getText().toString());
            values.put("rut", rut.getText().toString());
            values.put("edad", Integer.parseInt(edad.getText().toString()));
            values.put("tipo_auto", tipoAuto.getSelectedItem().toString());
            values.put("marca", marca.getSelectedItem().toString());
            values.put("modelo", modelo.getText().toString());
            values.put("patente", patente.getText().toString());
            values.put("causal", causal.getSelectedItem().toString());
            values.put("foto_path", fotoPath);
            values.put("nombre_funcionario", nombreFuncionario.getText().toString());
            values.put("numero_placa", numeroPlaca.getText().toString());
            values.put("departamento", departamento.getSelectedItem().toString());
            values.put("lugar_pago", lugarPago.getText().toString());

            // Insertar los datos en la tabla 'partes'
            long resultado = db.insert("partes", null, values);

            // Mostrar mensaje de éxito o error
            if (resultado != -1) {
                Toast.makeText(this, "Parte guardado correctamente", Toast.LENGTH_SHORT).show();
                finish(); // Cierra la actividad y vuelve al menú principal
            } else {
                Toast.makeText(this, "Error al guardar parte", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            // Mostrar mensaje de error si ocurre una excepción
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}