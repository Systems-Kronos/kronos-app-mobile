package com.example.kronosprojeto.dto;

public class ReportRequestDto {
    long idTarefa;
    long idUsuario;
    String descricao;
    String problema;
    String status;

    public long getIdTarefa() {
        return idTarefa;
    }

    public void setIdTarefa(Integer idTarefa) {
        this.idTarefa = idTarefa;
    }

    public long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getProblema() {
        return problema;
    }

    public void setProblema(String problema) {
        this.problema = problema;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ReportRequestDto(long idTarefa, long idUsuario, String descricao, String problema, String status) {
        this.idTarefa = idTarefa;
        this.idUsuario = idUsuario;
        this.descricao = descricao;
        this.problema = problema;
        this.status = status;
    }

    public ReportRequestDto() {
    }
}
