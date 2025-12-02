package com.example.aplicacionpartes;

public class RespuestaAdmin {
    public String texto;
    public long timestamp;
    public String autor;

    public RespuestaAdmin() {}

    public RespuestaAdmin(String texto, long timestamp, String autor) {
        this.texto = texto;
        this.timestamp = timestamp;
        this.autor = autor;
    }
}