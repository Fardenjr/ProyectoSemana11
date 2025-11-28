package com.example.aplicacionpartes;

public class Mensaje {
    public String texto;
    public String autor; // "usuario" o "bot"
    public long timestamp;

    // Constructor vacío para Firebase
    public Mensaje() {}

    public Mensaje(String texto, boolean esBot) {
        this.texto = texto;
        this.autor = esBot ? "bot" : "usuario";
        this.timestamp = System.currentTimeMillis();
    }
}