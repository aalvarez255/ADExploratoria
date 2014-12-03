package com.exploratoria.adexploratoria;

import android.graphics.Bitmap;

/**
 * Created by adrian on 19/11/14.
 */
public class ItemDrawer {
    private String titulo;
    private int year;
    private String tipo;
    private Bitmap portada;
    private String idm;

    public ItemDrawer(String titulo, int year, String tipo, Bitmap portada, String idm) {
        this.titulo = titulo;
        this.year = year;
        this.tipo = tipo;
        this.portada = portada;
        this.idm = idm;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getIdm() {
        return idm;
    }

    public void setIdm(String idm) {
        this.idm = idm;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Bitmap getPortada() {
        return portada;
    }

    public void setPortada(Bitmap portada) {
        this.portada = portada;
    }
}
