package com.example.aplicacionpartes;

public class Solicitud {
    public String mensaje;
    public long timestamp;
    public String uidUsuario; // se completa al leer desde Firebase

    public Solicitud() {
        // Constructor vacío requerido por Firebase
    }

    public Solicitud(String mensaje, long timestamp, String uidUsuario) {
        this.mensaje = mensaje;
        this.timestamp = timestamp;
        this.uidUsuario = uidUsuario;
    }
}