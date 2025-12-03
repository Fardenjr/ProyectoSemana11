package com.example.aplicacionpartes;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Clase helper para manejar operaciones comunes con Firebase.
 * Incluye autenticación, gestión de usuarios y partes, y validación de administradores.
 */
public class FirebaseHelper {

    private final DatabaseReference partesRef;
    private final DatabaseReference usuariosRef;

    // Correos autorizados como administradores (normalizados en minúsculas)
    public static final List<String> CORREOS_ADMIN = Arrays.asList(
            "admin@carabineros.cl",
            "sargento@carabineros.cl",
            "comandante@carabineros.cl",
            "fiscal@ministerio.cl",
            "jefe.transito@municipalidad.cl"
    ).stream().map(String::toLowerCase).collect(Collectors.toList());

    public FirebaseHelper() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        partesRef = db.getReference("partes");
        usuariosRef = db.getReference("usuarios");
    }

    // ==========================
    // AUTENTICACIÓN
    // ==========================

    public String getUsuarioActualId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null ? user.getUid() : "anonimo";
    }

    public String getUsuarioActualEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null ? user.getEmail() : null;
    }

    public boolean esAdmin() {
        String email = getUsuarioActualEmail();
        return esCorreoAdmin(email);
    }

    /**
     * Verifica si un correo pertenece a la lista de administradores.
     * @param email Correo a verificar.
     * @return true si es un correo de administrador, false en caso contrario.
     */
    public static boolean esCorreoAdmin(String email) {
        return email != null && CORREOS_ADMIN.contains(email.trim().toLowerCase());
    }

    // ==========================
    // USUARIOS
    // ==========================

    public void insertarUsuario(Usuario usuario, OnResultadoListener listener) {
        usuariosRef.child(usuario.uid).setValue(usuario)
                .addOnSuccessListener(aVoid -> listener.onExito())
                .addOnFailureListener(listener::onError);
    }

    public void obtenerUsuarioPorId(String uid, OnUsuarioListener listener) {
        usuariosRef.child(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Usuario usuario = task.getResult().getValue(Usuario.class);
                listener.onUsuarioObtenido(usuario);
            } else {
                listener.onError(task.getException());
            }
        });
    }

    public void obtenerTodosLosUsuarios(OnListaUsuariosListener listener) {
        usuariosRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Usuario> lista = new ArrayList<>();
                for (DataSnapshot snap : task.getResult().getChildren()) {
                    Usuario usuario = snap.getValue(Usuario.class);
                    if (usuario != null) lista.add(usuario);
                }
                listener.onUsuariosObtenidos(lista);
            } else {
                listener.onError(task.getException());
            }
        });
    }

    // ==========================
    // PARTES
    // ==========================

    // Crear parte: permitido para cualquier usuario autenticado
    public void insertarParte(Parte parte, OnResultadoListener listener) {
        String id = partesRef.push().getKey();
        parte.id = id;
        partesRef.child(id).setValue(parte)
                .addOnSuccessListener(aVoid -> listener.onExito())
                .addOnFailureListener(listener::onError);
    }

    // Leer parte por ID
    public void obtenerPartePorId(String parteId, OnParteListener listener) {
        partesRef.child(parteId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Parte parte = task.getResult().getValue(Parte.class);
                listener.onParteObtenido(parte);
            } else {
                listener.onError(task.getException());
            }
        });
    }

    // Leer todos los partes
    public void obtenerTodosLosPartes(OnListaPartesListener listener) {
        partesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Parte> lista = new ArrayList<>();
                for (DataSnapshot snap : task.getResult().getChildren()) {
                    Parte parte = snap.getValue(Parte.class);
                    if (parte != null) lista.add(parte);
                }
                listener.onPartesObtenidos(lista);
            } else {
                listener.onError(task.getException());
            }
        });
    }

    // Metodo puente para ListaEditableActivity
    public void obtenerPartes(OnPartesListener listener) {
        obtenerTodosLosPartes(new OnListaPartesListener() {
            @Override
            public void onPartesObtenidos(List<Parte> partes) {
                listener.onPartesObtenidos(partes);
            }

            @Override
            public void onError(Exception e) {
                listener.onError(e);
            }
        });
    }

    // Editar parte: solo administradores
    public void updateParte(String parteId, Parte parte, OnResultadoListener listener) {
        if (esAdmin()) {
            partesRef.child(parteId).setValue(parte)
                    .addOnSuccessListener(aVoid -> listener.onExito())
                    .addOnFailureListener(listener::onError);
        } else {
            listener.onError(new Exception("No tienes permisos para editar partes"));
        }
    }

    // Borrar parte: solo administradores
    public void borrarPartePorId(String parteId, OnResultadoListener listener) {
        if (esAdmin()) {
            partesRef.child(parteId).removeValue()
                    .addOnSuccessListener(aVoid -> listener.onExito())
                    .addOnFailureListener(listener::onError);
        } else {
            listener.onError(new Exception("No tienes permisos para eliminar partes"));
        }
    }

    // ==========================
    // INTERFACES DE CALLBACK
    // ==========================

    public interface OnResultadoListener {
        void onExito();
        void onError(Exception e);
    }

    public interface OnParteListener {
        void onParteObtenido(Parte parte);
        void onError(Exception e);
    }

    public interface OnListaPartesListener {
        void onPartesObtenidos(List<Parte> partes);
        void onError(Exception e);
    }

    public interface OnPartesListener {
        void onPartesObtenidos(List<Parte> partes);
        void onError(Exception e);
    }

    public interface OnUsuarioListener {
        void onUsuarioObtenido(Usuario usuario);
        void onError(Exception e);
    }

    public interface OnListaUsuariosListener {
        void onUsuariosObtenidos(List<Usuario> usuarios);
        void onError(Exception e);
    }
}