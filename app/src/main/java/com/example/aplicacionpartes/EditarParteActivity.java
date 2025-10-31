package com.example.aplicacionpartes;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;

public class EditarParteActivity extends AppCompatActivity {

    // Campos del formulario
    EditText nombres, apellidos, rut, edad, modelo, patente;
    Spinner tipoAuto, marca, causal;
    ImageView fotoVehiculo;
    Button btnActualizar, btnFoto;

    // Ruta de la foto y ID del parte a editar
    String fotoPath = "";
    int parteId;

    // Acceso a la base de datos
    DBHelper dbHelper;
    static final int REQUEST_IMAGE_CAPTURE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Reutiliza el layout de creación de parte
        setContentView(R.layout.activity_parte);

        // Enlaza los elementos visuales del layout
        nombres = findViewById(R.id.nombres);
        apellidos = findViewById(R.id.apellidos);
        rut = findViewById(R.id.rut);
        edad = findViewById(R.id.edad);
        modelo = findViewById(R.id.modelo);
        patente = findViewById(R.id.patente);
        tipoAuto = findViewById(R.id.tipoAuto);
        marca = findViewById(R.id.marca);
        causal = findViewById(R.id.causal);
        fotoVehiculo = findViewById(R.id.fotoVehiculo);
        btnActualizar = findViewById(R.id.btnGuardar); // Reutiliza el botón de guardar
        btnFoto = findViewById(R.id.btnFoto);

        // Cambia el texto del botón para indicar que es una edición
        btnActualizar.setText("Actualizar Parte");

        // Inicializa la base de datos
        dbHelper = new DBHelper(this);

        // Carga las opciones en los spinners
        cargarSpinners();

        // Obtiene el ID del parte desde el intent
        parteId = getIntent().getIntExtra("parteId", -1);
        if (parteId == -1) {
            Toast.makeText(this, "ID de parte no recibido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Carga los datos del parte desde la base
        cargarDatosParte(parteId);

        // Configura los botones
        btnFoto.setOnClickListener(v -> abrirCamara());
        btnActualizar.setOnClickListener(v -> actualizarParte());
    }

    // Carga las opciones en los spinners y asigna adaptadores
    private void cargarSpinners() {
        String[] tipos = {"SUV", "Sedán", "Camioneta", "Hatchback"};
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
        String[] causales = {
                "No respetar disco Pare", "Conducir sin licencia", "Conducir usando el celular",
                "No respetar luz roja del semáforo", "Vehículo sin placa patente", "Exceso de velocidad (20-60 km/h)",
                "Conducir sin velocímetro", "Conducir sin espejo retrovisor", "Acceder a beneficios de transporte sin ser titular",
                "Revisión técnica vencida o rechazada", "Licencia distinta a la requerida", "Sin permiso de circulación o SOAP",
                "Conducir en condiciones físicas deficientes", "Adelantar en paso peatonal o berma", "Desobedecer señales de Carabineros",
                "Circular contra el sentido del tránsito", "Estacionar en puentes, túneles o cruces", "Vehículo sin luces o frenos en mal estado",
                "Circular con neumáticos defectuosos", "No bajar luces en carretera", "Usar elementos para evadir fiscalización",
                "Estacionar en lugar exclusivo para discapacitados", "Estacionar en doble fila", "Cruzar vía férrea en lugar no autorizado",
                "Exceso de carga o pasajeros", "Estacionar a menos de 10 metros de la esquina", "Conducir marcha atrás en cruce",
                "No hacer señales antes de virar", "Conducir sin silenciador o escape en mal estado", "Negarse a transportar escolares",
                "Arrojar objetos desde el vehículo", "No detenerse ante línea férrea", "Circular con puertas abiertas en locomoción colectiva",
                "No respetar normas sobre transporte de pasajeros", "No respetar normas de pista de circulación",
                "Obstrucción visual por carga o pasajeros", "Transitar sin TAG", "Circular por vías exclusivas de transporte público",
                "Participar en carreras clandestinas", "No respetar indicaciones de Carabineros", "Circular en días de restricción vehicular"
        };

        // Asignación de adaptadores a los spinners
        tipoAuto.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tipos));
        marca.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, marcas));
        causal.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, causales));
    }

    // Carga los datos del parte desde la base de datos
    private void cargarDatosParte(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM partes WHERE id = ?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            nombres.setText(cursor.getString(cursor.getColumnIndexOrThrow("nombres")));
            apellidos.setText(cursor.getString(cursor.getColumnIndexOrThrow("apellidos")));
            rut.setText(cursor.getString(cursor.getColumnIndexOrThrow("rut")));
            edad.setText(cursor.getString(cursor.getColumnIndexOrThrow("edad")));
            modelo.setText(cursor.getString(cursor.getColumnIndexOrThrow("modelo")));
            patente.setText(cursor.getString(cursor.getColumnIndexOrThrow("patente")));
            fotoPath = cursor.getString(cursor.getColumnIndexOrThrow("foto_path"));

            // Carga la imagen si existe
            if (fotoPath != null && !fotoPath.isEmpty()) {
                fotoVehiculo.setImageURI(Uri.fromFile(new File(fotoPath)));
            }

            // Selecciona los valores en los spinners
            seleccionarSpinner(tipoAuto, cursor.getString(cursor.getColumnIndexOrThrow("tipo_auto")));
            seleccionarSpinner(marca, cursor.getString(cursor.getColumnIndexOrThrow("marca")));
            seleccionarSpinner(causal, cursor.getString(cursor.getColumnIndexOrThrow("causal")));
        }
        cursor.close();
    }

    // Selecciona el valor correcto en un spinner
    private void seleccionarSpinner(Spinner spinner, String valor) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        if (adapter != null) {
            int index = adapter.getPosition(valor);
            if (index >= 0) {
                spinner.setSelection(index);
            }
        }
    }

    // Abre la cámara para tomar una nueva foto
    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File foto = crearArchivoImagen();
            Uri fotoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", foto);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    // Crea un archivo para guardar la imagen
    private File crearArchivoImagen() {
        String nombreArchivo = "FOTO_" + System.currentTimeMillis();
        File directorio = getFilesDir(); // Guarda en almacenamiento interno privado
        File imagen = new File(directorio, nombreArchivo + ".jpg");
        fotoPath = imagen.getAbsolutePath(); // Guarda la ruta para mostrarla luego
        return imagen;
    }

    // Muestra la imagen tomada en el ImageView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            fotoVehiculo.setImageURI(Uri.fromFile(new File(fotoPath)));
        }
    }

    // Actualiza el parte en la base de datos con los nuevos valores
    private void actualizarParte() {
        try {
            ContentValues values = new ContentValues();
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

            boolean actualizado = dbHelper.updateParte(parteId, values);
            if (actualizado) {
                Toast.makeText(this, "Parte actualizado correctamente", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al actualizar parte", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


}
