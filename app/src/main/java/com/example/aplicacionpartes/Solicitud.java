// Solicitud.java
package com.example.aplicacionpartes;

import java.io.Serializable;

public class Solicitud {
    public String id;
    public String uidUsuario;
    public String nombre;
    public String apellido;
    public String cargo;
    public String institucion;
    public String mensaje; // este es el campo que representa el motivo
    public long timestamp;

    public Solicitud() {}
}