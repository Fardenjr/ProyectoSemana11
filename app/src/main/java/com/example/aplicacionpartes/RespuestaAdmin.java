package com.example.aplicacionpartes;

public class RespuestaAdmin {
    public String mensaje;
    public long timestamp;
    public String autor; // normalmente "admin"

    public RespuestaAdmin() {
        // Constructor vacío requerido por Firebase
    }

    public RespuestaAdmin(String mensaje, long timestamp, String autor) {
        this.mensaje = mensaje;
        this.timestamp = timestamp;
        this.autor = autor;
    }
}