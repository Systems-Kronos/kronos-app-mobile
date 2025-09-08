package com.example.kronosprojeto.model;

public class AssignmentHistory {

    private String titulo;
    private String usuario;
    private String descricao;


    public String getTitulo() {
        return titulo;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getDescricao() {
        return descricao;
    }
    public AssignmentHistory(String titulo, String descricao, String usuario) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.usuario = usuario;
    }
}


