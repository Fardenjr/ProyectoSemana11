package com.example.aplicacionpartes;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

public class VistaParteActivity extends AppCompatActivity {

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parte); // Reutilizamos el layout de edición

        dbHelper = new DBHelper(this);

        // Obtener el ID del parte desde el intent
        int parteId = getIntent().getIntExtra("parteId", -1);
        if (parteId == -1) return;

        // Consultar el parte en la base de datos
        Parte parte = dbHelper.obtenerPartePorId(parteId);
        if (parte == null) return;

        // Enlazar los spinners
        Spinner tipoAuto = findViewById(R.id.tipoAuto);
        Spinner marca = findViewById(R.id.marca);
        Spinner causal = findViewById(R.id.causal);
        Spinner departamento = findViewById(R.id.departamento);

        // Cargar adaptadores desde arrays.xml
        ArrayAdapter<CharSequence> adapterTipo = ArrayAdapter.createFromResource(
                this, R.array.tipos_vehiculo, android.R.layout.simple_spinner_item);
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipoAuto.setAdapter(adapterTipo);

        ArrayAdapter<CharSequence> adapterMarca = ArrayAdapter.createFromResource(
                this, R.array.marcas_vehiculo, android.R.layout.simple_spinner_item);
        adapterMarca.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        marca.setAdapter(adapterMarca);

        ArrayAdapter<CharSequence> adapterCausal = ArrayAdapter.createFromResource(
                this, R.array.causales_infraccion, android.R.layout.simple_spinner_item);
        adapterCausal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        causal.setAdapter(adapterCausal);

        ArrayAdapter<CharSequence> adapterDepto = ArrayAdapter.createFromResource(
                this, R.array.departamentos, android.R.layout.simple_spinner_item);
        adapterDepto.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departamento.setAdapter(adapterDepto);

        // Seleccionar el valor correspondiente en cada spinner
        tipoAuto.setSelection(adapterTipo.getPosition(parte.tipo_auto));
        marca.setSelection(adapterMarca.getPosition(parte.marca));
        causal.setSelection(adapterCausal.getPosition(parte.causal));
        departamento.setSelection(adapterDepto.getPosition(parte.departamento));

        // Desactivar los spinners (modo solo lectura)
        tipoAuto.setEnabled(false);
        marca.setEnabled(false);
        causal.setEnabled(false);
        departamento.setEnabled(false);

        // Enlazar campos de texto
        EditText nombres = findViewById(R.id.nombres);
        EditText apellidos = findViewById(R.id.apellidos);
        EditText rut = findViewById(R.id.rut);
        EditText edad = findViewById(R.id.edad);
        EditText modelo = findViewById(R.id.modelo);
        EditText patente = findViewById(R.id.patente);
        EditText nombreFuncionario = findViewById(R.id.nombreFuncionario);
        EditText numeroPlaca = findViewById(R.id.numeroPlaca);
        EditText lugarPago = findViewById(R.id.lugarPago);

        // Enlazar imagen y botones
        ImageView fotoVehiculo = findViewById(R.id.fotoVehiculo);
        Button btnFoto = findViewById(R.id.btnFoto);
        Button btnGuardar = findViewById(R.id.btnGuardar);

        // Cargar datos en los campos
        nombres.setText(parte.nombres);
        apellidos.setText(parte.apellidos);
        rut.setText(parte.rut);
        edad.setText(String.valueOf(parte.edad));
        modelo.setText(parte.modelo);
        patente.setText(parte.patente);
        nombreFuncionario.setText(parte.nombre_funcionario);
        numeroPlaca.setText(parte.numero_placa);
        lugarPago.setText(parte.lugar_pago);

        // Mostrar imagen si existe URI válida
        if (parte.foto_path != null && !parte.foto_path.isEmpty()) {
            Uri uri = Uri.parse(parte.foto_path);
            fotoVehiculo.setImageURI(uri);
        } else {
            fotoVehiculo.setImageResource(android.R.drawable.ic_menu_report_image); // opcional
        }

        // Desactivar campos de texto (modo solo lectura)
        nombres.setEnabled(false);
        apellidos.setEnabled(false);
        rut.setEnabled(false);
        edad.setEnabled(false);
        modelo.setEnabled(false);
        patente.setEnabled(false);
        nombreFuncionario.setEnabled(false);
        numeroPlaca.setEnabled(false);
        lugarPago.setEnabled(false);

        // Ocultar botones de acción
        btnGuardar.setVisibility(View.GONE);
        btnFoto.setVisibility(View.GONE);
    }
}