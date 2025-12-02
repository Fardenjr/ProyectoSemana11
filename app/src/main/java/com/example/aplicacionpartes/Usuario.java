package com.example.aplicacionpartes;

public class Usuario {
    public String uid;
    public String nombre;
    public String apellido;
    public String cargo;
    public String ciudad;
    public String email;
    public String rol; // "admin" o "usuario"

    // Constructor vacío requerido por Firebase
    public Usuario() {}

    public Usuario(String uid, String nombre, String apellido, String cargo,
                   String ciudad, String email, String rol) {
        this.uid = uid;
        this.nombre = nombre;
        this.apellido = apellido;
        this.cargo = cargo;
        this.ciudad = ciudad;
        this.email = email;
        this.rol = rol;
    }
}