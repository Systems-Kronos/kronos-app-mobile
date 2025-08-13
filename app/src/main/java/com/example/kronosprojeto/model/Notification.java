package com.example.kronosprojeto.model;

public class Notification {

    private String titulo;
    private String descrição;

    public String getTitulo() {
        return titulo;
    }

    public String getDescrição() {
        return descrição;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescrição(String descrição) {
        this.descrição = descrição;
    }

    public Notification() {
    }

    public Notification(String titulo, String descrição) {
        this.titulo = titulo;
        this.descrição = descrição;
    }
}
