package com.padron.kinnov;

/**
 * Created by antonio on 24/04/16.
 */
public class Protocolo {
    private String Titulo;
    private String Descripcion;

    public Protocolo(String titulo,String descripcion) {
        Descripcion = descripcion;
        Titulo = titulo;
    }

    public String getTitulo() {
        return Titulo;
    }

    public String getDescripcion() {
        return Descripcion;
    }
}
