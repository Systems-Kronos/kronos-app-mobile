package com.example.kronosprojeto.model;

public class AssignmentHistory {

    private String titulo;
    private String nomeUsuarioAtribuido;
    private String descricao;


    public String getTitulo() {
        return titulo;
    }

    public String getUsuario() {
        return nomeUsuarioAtribuido;
    }

    public String getDescricao() {
        return descricao;
    }
    public AssignmentHistory(String titulo, String descricao, String usuario) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.nomeUsuarioAtribuido = usuario;
    }
}


