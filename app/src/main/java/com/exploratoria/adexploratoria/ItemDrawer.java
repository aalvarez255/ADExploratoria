package com.exploratoria.adexploratoria;

/**
 * Created by adrian on 19/11/14.
 */
public class ItemDrawer {
    private String titulo;
    private int year;
    private String tipo;
    private int portada;

    public ItemDrawer(String titulo, int year, String tipo, int portada) {
        this.titulo = titulo;
        this.year = year;
        this.tipo = tipo;
        this.portada = portada;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getPortada() {
        return portada;
    }

    public void setPortada(int portada) {
        this.portada = portada;
    }
}
