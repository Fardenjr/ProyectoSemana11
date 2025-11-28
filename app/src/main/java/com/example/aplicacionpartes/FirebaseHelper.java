package com.example.aplicacionpartes;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseHelper {

    private final DatabaseReference partesRef;

    // Correos autorizados como administradores
    private static final List<String> CORREOS_ADMIN = Arrays.asList(
            "admin@carabineros.cl",
            "sargento@carabineros.cl",
            "comandante@carabineros.cl",
            "fiscal@ministerio.cl",
            "jefe.transito@municipalidad.cl"
    );

    public FirebaseHelper() {
        partesRef = FirebaseDatabase.getInstance().getReference("partes");
    }

    // Obtener ID del usuario actual
    public String getUsuarioActualId() {
        return FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "anonimo";
    }

    // Obtener correo del usuario actual
    public String getUsuarioActualEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null ? user.getEmail() : null;
    }

    // Verificar si el usuario es admin
    public boolean esAdmin() {
        String email = getUsuarioActualEmail();
        return email != null && CORREOS_ADMIN.contains(email);
    }

    // Insertar parte
    public void insertarParte(Parte parte, OnResultadoListener listener) {
        String id = partesRef.push().getKey();
        parte.id = id;
        partesRef.child(id).setValue(parte)
                .addOnSuccessListener(aVoid -> listener.onExito())
                .addOnFailureListener(listener::onError);
    }

    // Obtener parte por ID
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

    // Obtener todos los partes
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

    // Alias para ListaEditableActivity
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

    // Actualizar parte existente
    public void updateParte(String parteId, Parte parte, OnResultadoListener listener) {
        partesRef.child(parteId).setValue(parte)
                .addOnSuccessListener(aVoid -> listener.onExito())
                .addOnFailureListener(listener::onError);
    }

    // Eliminar parte (solo admins)
    public void borrarPartePorId(String parteId) {
        if (esAdmin()) {
            partesRef.child(parteId).removeValue();
        }
    }

    public void borrarPartePorId(String parteId, OnResultadoListener listener) {
        if (esAdmin()) {
            partesRef.child(parteId).removeValue()
                    .addOnSuccessListener(aVoid -> listener.onExito())
                    .addOnFailureListener(listener::onError);
        } else {
            listener.onError(new Exception("No tienes permisos para eliminar partes"));
        }
    }

    // Interfaces de callback
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
}