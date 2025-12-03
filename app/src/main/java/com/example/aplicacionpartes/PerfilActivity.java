package com.example.aplicacionpartes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class PerfilActivity extends AppCompatActivity {

    private TextView txtNombre, txtApellido, txtCargo, txtCiudad, txtCorreoUsuario;
    private Button btnCerrarSesion;

    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        Toolbar toolbar = findViewById(R.id.perfil_toolbar);
        setSupportActionBar(toolbar);

        txtNombre = findViewById(R.id.txtNombre);
        txtApellido = findViewById(R.id.txtApellido);
        txtCargo = findViewById(R.id.txtCargo);
        txtCiudad = findViewById(R.id.txtCiudad);
        txtCorreoUsuario = findViewById(R.id.txtCorreoUsuario);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        firebaseHelper = new FirebaseHelper();

        FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioActual != null) {
            String uid = usuarioActual.getUid();

            firebaseHelper.obtenerUsuarioPorId(uid, new FirebaseHelper.OnUsuarioListener() {
                @Override
                public void onUsuarioObtenido(Usuario usuario) {
                    if (usuario != null) {
                        txtNombre.setText("Nombre: " + usuario.nombre);
                        txtApellido.setText("Apellido: " + usuario.apellido);
                        txtCargo.setText("Cargo: " + usuario.cargo);
                        txtCiudad.setText("Ciudad: " + usuario.ciudad);
                        txtCorreoUsuario.setText("Correo: " + usuario.email);
                    }
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(PerfilActivity.this, "Error cargando perfil", Toast.LENGTH_SHORT).show();
                }
            });
        }

        btnCerrarSesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            finish();
        });
    }
}