package com.example.aplicacionpartes;

public class ChatMessage {
    public String remitente;
    public String texto;
    public long timestamp;

    public ChatMessage() {}

    public ChatMessage(String remitente, String texto, long timestamp) {
        this.remitente = remitente;
        this.texto = texto;
        this.timestamp = timestamp;
    }
}