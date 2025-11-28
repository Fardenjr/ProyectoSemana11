package com.example.aplicacionpartes;

public class Usuario {
    public String uid;
    public String nombre;
    public String email;
    public String tipo;   // carabinero, direcciondetransito, admin
    public String rango;  // Sargento, Comandante, Fiscal, etc.

    public Usuario() {}

    public Usuario(String uid, String nombre, String email, String tipo, String rango) {
        this.uid = uid;
        this.nombre = nombre;
        this.email = email;
        this.tipo = tipo;
        this.rango = rango;
    }
}