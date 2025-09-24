package com.example.kronosprojeto.model;

public class Notification {

    private String cMensagem;
    private String dDataCriacao;


    public String getTitulo() {
        return cMensagem;
    }

    public String getDescrição() {
        return dDataCriacao;
    }




    public Notification() {
    }

    public Notification(String titulo, String descrição) {
        this.dDataCriacao = titulo;
        this.cMensagem = descrição;
    }
}
