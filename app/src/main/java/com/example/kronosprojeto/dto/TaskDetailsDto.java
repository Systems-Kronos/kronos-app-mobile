package com.example.kronosprojeto.dto;

public class TaskDetailsDto {
    private int id;
    private String nome;
    private UserResponseDto usuarioRelator;
    private int gravidade;
    private int urgencia;
    private int tendencia;
    private int tempoEstimado;
    private String descricao;
    private String status;
    private String dataAtribuicao;
    private String dataConclusao;
    private String origemTarefa;
    private String dataPrazo;

    public String getDataPrazo() {
        return dataPrazo;
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
    public UserResponseDto getUsuarioRelator() {
        return usuarioRelator;
    }
    public String getDescricao() {
        return descricao;
    }

    public String getStatus() {
        return status;
    }
    public String getDataAtribuicao() {
        return dataAtribuicao;
    }
    public int getGravidade() {
        return gravidade;
    }
    public int getUrgencia() {
        return urgencia;
    }
    public int getTendencia() {
        return tendencia;
    }
    public int getTempoEstimado() {
        return tempoEstimado;
    }
    public String getDataConclusao() {
        return dataConclusao;
    }
    public String getOrigemTarefa() {
        return origemTarefa;
    }
}
