package com.example.kronosprojeto.Model;

import java.util.Date;

public class Tarefa {
    protected String titulo;
    protected Date dia;
    protected int prioridade;
    protected String setor;

    String tag;

    public Tarefa(String titulo, Date dia, int prioridade, String setor, String tag) {
        this.titulo = titulo;
        this.dia = dia;
        this.prioridade = prioridade;
        this.setor = setor;
        this.tag = tag;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public Date getDia() {
        return dia;
    }

    public String getTag() {
        return tag;
    }

    public String getSetor() {
        return setor;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setDia(Date dia) {
        this.dia = dia;
    }

    public void setPrioridade(int prioridade) {
        this.prioridade = prioridade;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
}
