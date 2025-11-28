package com.example.aplicacionpartes;

public class Parte {
    public String id;
    public String nombres;
    public String apellidos;
    public String rut;
    public int edad;
    public String tipo_auto;
    public String marca;
    public String modelo;
    public String patente;
    public String causal;
    public String gravedad;
    public String foto_path;
    public String nombre_funcionario;
    public String numero_placa;
    public String departamento;
    public String lugar_pago;
    public String autorId;

    public Parte() {}

    public Parte(String id, String nombres, String apellidos, String rut, int edad,
                 String tipo_auto, String marca, String modelo, String patente,
                 String causal, String gravedad, String foto_path,
                 String nombre_funcionario, String numero_placa,
                 String departamento, String lugar_pago, String autorId) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.rut = rut;
        this.edad = edad;
        this.tipo_auto = tipo_auto;
        this.marca = marca;
        this.modelo = modelo;
        this.patente = patente;
        this.causal = causal;
        this.gravedad = gravedad;
        this.foto_path = foto_path;
        this.nombre_funcionario = nombre_funcionario;
        this.numero_placa = numero_placa;
        this.departamento = departamento;
        this.lugar_pago = lugar_pago;
        this.autorId = autorId;
    }
}