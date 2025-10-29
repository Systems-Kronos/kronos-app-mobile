package com.example.kronosprojeto.model;

public class Notification {

    private String cMensagem;
    private String dCriacao;


    public String getTitulo() {
        return cMensagem;
    }

    public String getDescrição() {
        return dCriacao;
    }




    public Notification() {
    }

    public Notification(String titulo, String descrição) {
        this.dCriacao = titulo;
        this.cMensagem = descrição;
    }
}
