package com.example.kronosprojeto.model;

import com.example.kronosprojeto.dto.UserResponseDto;

public class Task {

    private int id;
    private String titulo;
    private Long usuarioRelator;
    private int gravidade;
    private int urgencia;
    private int tendencia;
    private int tempoEstimado;
    private String descricao;
    private String status;
    private String dataAtribuicao;
    private String dataConclusao;
    private String origemTarefa;

    // Construtor completo
    public Task(int id, String nome, Long usuarioRelator, int gravidade, int urgencia, int tendencia,
                int tempoEstimado, String descricao, String status, String dataAtribuicao,
                String dataConclusao, String origemTarefa) {
        this.id = id;
        this.titulo = nome;
        this.usuarioRelator = usuarioRelator;
        this.gravidade = gravidade;
        this.urgencia = urgencia;
        this.tendencia = tendencia;
        this.tempoEstimado = tempoEstimado;
        this.descricao = descricao;
        this.status = status;
        this.dataAtribuicao = dataAtribuicao;
        this.dataConclusao = dataConclusao;
        this.origemTarefa = origemTarefa;
    }

    // Construtor simplificado
    public Task(String nome, String dataAtribuicao, int gravidade, int urgencia, int tendencia,
                Long usuarioRelator, String origemTarefa, String descricao, String dataConclusao) {
        this.titulo = nome;
        this.dataAtribuicao = dataAtribuicao;
        this.gravidade = gravidade;
        this.urgencia = urgencia;
        this.tendencia = tendencia;
        this.usuarioRelator = usuarioRelator;
        this.origemTarefa = origemTarefa;
        this.descricao = descricao;
        this.dataConclusao = dataConclusao;
    }

    public Task() {}

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return titulo; }
    public void setNome(String nome) { this.titulo = nome; }

    public Long getUsuarioRelator() { return usuarioRelator; }
    public void setUsuarioRelator(Long usuarioRelator) { this.usuarioRelator = usuarioRelator; }

    public int getGravidade() { return gravidade; }
    public void setGravidade(int gravidade) { this.gravidade = gravidade; }

    public int getUrgencia() { return urgencia; }
    public void setUrgencia(int urgencia) { this.urgencia = urgencia; }

    public int getTendencia() { return tendencia; }
    public void setTendencia(int tendencia) { this.tendencia = tendencia; }

    public int getTempoEstimado() { return tempoEstimado; }
    public void setTempoEstimado(int tempoEstimado) { this.tempoEstimado = tempoEstimado; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDataAtribuicao() { return dataAtribuicao; }
    public void setDataAtribuicao(String dataAtribuicao) { this.dataAtribuicao = dataAtribuicao; }

    public String getDataConclusao() { return dataConclusao; }
    public void setDataConclusao(String dataConclusao) { this.dataConclusao = dataConclusao; }

    public String getOrigemTarefa() { return origemTarefa; }
    public void setOrigemTarefa(String origemTarefa) { this.origemTarefa = origemTarefa; }
}
