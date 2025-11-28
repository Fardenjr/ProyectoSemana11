package com.example.aplicacionpartes;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class EditarParteActivity extends AppCompatActivity {

    EditText nombres, apellidos, rut, edad, modelo, patente;
    Spinner tipoAuto, marca, causal;
    ImageView fotoVehiculo;
    Button btnActualizar, btnFoto;

    String fotoPath = "";
    String parteId;

    FirebaseHelper firebaseHelper;
    static final int REQUEST_IMAGE_CAPTURE = 2;

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
        fotoVehiculo = findViewById(R.id.fotoVehiculo);
        btnActualizar = findViewById(R.id.btnGuardar);
        btnFoto = findViewById(R.id.btnFoto);

        btnActualizar.setText("Actualizar Parte");

        firebaseHelper = new FirebaseHelper();

        parteId = getIntent().getStringExtra("parteId");
        if (parteId == null) {
            Toast.makeText(this, "ID de parte no recibido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cargarSpinners();
        cargarDatosParte(parteId);

        btnFoto.setOnClickListener(v -> abrirCamara());
        btnActualizar.setOnClickListener(v -> actualizarParte());
    }

    private void cargarSpinners() {
        String[] tipos = {"SUV", "Sedán", "Camioneta", "Hatchback"};
        String[] marcas = {"Toyota", "Hyundai", "Chevrolet", "Kia", "Nissan", "Volkswagen", "Ford", "Honda"};
        String[] causales = {"Exceso de velocidad", "No respetar señal", "Conducir sin licencia"};

        tipoAuto.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tipos));
        marca.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, marcas));
        causal.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, causales));
    }

    private void cargarDatosParte(String id) {
        firebaseHelper.obtenerPartePorId(id, new FirebaseHelper.OnParteListener() {
            @Override
            public void onParteObtenido(Parte parte) {
                if (parte == null) return;

                nombres.setText(parte.nombres);
                apellidos.setText(parte.apellidos);
                rut.setText(parte.rut);
                edad.setText(String.valueOf(parte.edad));
                modelo.setText(parte.modelo);
                patente.setText(parte.patente);
                fotoPath = parte.foto_path;

                if (fotoPath != null && !fotoPath.isEmpty()) {
                    fotoVehiculo.setImageURI(Uri.fromFile(new File(fotoPath)));
                }

                seleccionarSpinner(tipoAuto, parte.tipo_auto);
                seleccionarSpinner(marca, parte.marca);
                seleccionarSpinner(causal, parte.causal);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(EditarParteActivity.this, "Error al cargar parte", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void seleccionarSpinner(Spinner spinner, String valor) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        if (adapter != null) {
            int index = adapter.getPosition(valor);
            if (index >= 0) {
                spinner.setSelection(index);
            }
        }
    }

    private void abrirCamara() {
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

    private void actualizarParte() {
        Parte parte = new Parte();

        parte.id = parteId;
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

        parte.autorId = firebaseHelper.getUsuarioActualId(); // opcional si quieres registrar quién lo editó

        firebaseHelper.updateParte(parteId, parte, new FirebaseHelper.OnResultadoListener() {
            @Override
            public void onExito() {
                Toast.makeText(EditarParteActivity.this, "Parte actualizado correctamente", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(EditarParteActivity.this, "Error al actualizar parte", Toast.LENGTH_LONG).show();
            }
        });
    }

    public static class ChatBotAdapter extends RecyclerView.Adapter<ChatBotAdapter.ViewHolder> {

        private final List<Mensaje> mensajes;
        private final Context context;

        public ChatBotAdapter(List<Mensaje> mensajes, Context context) {
            this.mensajes = mensajes;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_mensaje, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Mensaje mensaje = mensajes.get(position);
            holder.txtMensaje.setText(mensaje.texto);

            String hora = DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date(mensaje.timestamp));
            holder.txtHora.setText(hora);

            if ("bot".equals(mensaje.autor)) {
                holder.txtMensaje.setBackgroundResource(R.drawable.bg_mensaje_bot);
                holder.txtMensaje.setTextColor(ContextCompat.getColor(context, android.R.color.black));
                holder.container.setGravity(android.view.Gravity.START);
                holder.labelAutor.setText("Bot");
            } else {
                holder.txtMensaje.setBackgroundResource(R.drawable.bg_mensaje_usuario);
                holder.txtMensaje.setTextColor(ContextCompat.getColor(context, android.R.color.white));
                holder.container.setGravity(android.view.Gravity.END);
                holder.labelAutor.setText("Tú");
            }
        }

        @Override
        public int getItemCount() {
            return mensajes.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView txtMensaje, txtHora, labelAutor;
            LinearLayout container;

            public ViewHolder(View itemView) {
                super(itemView);
                txtMensaje = itemView.findViewById(R.id.txtMensaje);
                txtHora = itemView.findViewById(R.id.txtHora);
                labelAutor = itemView.findViewById(R.id.labelAutor);
                container = itemView.findViewById(R.id.containerMensaje);
            }
        }
    }
}