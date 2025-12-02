// Solicitud.java
package com.example.aplicacionpartes;

import java.io.Serializable;

public class Solicitud implements Serializable {
    public String uidUsuario;
    public String nombre;
    public String apellido;
    public String cargo;
    public String institucion;
    public String mensaje;
    public String id;
    public long timestamp;

    public Solicitud() {}
}