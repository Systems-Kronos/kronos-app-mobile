package com.example.kronosprojeto.dto;

public class TaskDetailsDto {
    private int id;
    private String nome;
    private UserResponseDto usuarioRelator; // String
    private int gravidade;
    private int urgencia;
    private int tendencia;
    private int tempoEstimado;
    private String descricao;
    private String status;
    private String dataAtribuicao; // agora String
    private String dataConclusao;   // agora String
    private String origemTarefa;
    private String dataPrazo;

    public String getDataPrazo() {
        return dataPrazo;
    }

    public void setDataPrazo(String dataPrazo) {
        this.dataPrazo = dataPrazo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public UserResponseDto getUsuarioRelator() {
        return usuarioRelator;
    }

    public void setUsuarioRelator(UserResponseDto usuarioRelator) {
        this.usuarioRelator = usuarioRelator;
    }

    public int getGravidade() {
        return gravidade;
    }

    public void setGravidade(int gravidade) {
        this.gravidade = gravidade;
    }

    public int getUrgencia() {
        return urgencia;
    }

    public void setUrgencia(int urgencia) {
        this.urgencia = urgencia;
    }

    public int getTendencia() {
        return tendencia;
    }

    public void setTendencia(int tendencia) {
        this.tendencia = tendencia;
    }

    public int getTempoEstimado() {
        return tempoEstimado;
    }

    public void setTempoEstimado(int tempoEstimado) {
        this.tempoEstimado = tempoEstimado;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDataAtribuicao() {
        return dataAtribuicao;
    }

    public void setDataAtribuicao(String dataAtribuicao) {
        this.dataAtribuicao = dataAtribuicao;
    }

    public String getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(String dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    public String getOrigemTarefa() {
        return origemTarefa;
    }

    public void setOrigemTarefa(String origemTarefa) {
        this.origemTarefa = origemTarefa;
    }
}
