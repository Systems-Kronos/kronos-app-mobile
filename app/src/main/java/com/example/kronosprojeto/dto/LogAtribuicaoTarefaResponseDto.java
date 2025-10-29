package com.example.kronosprojeto.dto;

public class LogAtribuicaoTarefaResponseDto {
    private Long id;
    private Long idTarefa;
    private Long idUsuarioAtribuido;
    private String nomeUsuarioAtribuido;
    private String observacao;
    private String dataRealocacao;

    public LogAtribuicaoTarefaResponseDto(Long idTarefa, Long idUsuarioAtribuido, String observacao, String dataRealocacao, String nomeUsuarioAtribuido) {
        this.idTarefa = idTarefa;
        this.idUsuarioAtribuido = idUsuarioAtribuido;
        this.observacao = observacao;
        this.nomeUsuarioAtribuido = nomeUsuarioAtribuido;
        this.dataRealocacao = dataRealocacao;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdTarefa() { return idTarefa; }
    public void setIdTarefa(Long idTarefa) { this.idTarefa = idTarefa; }

    public String getNomeUsuarioAtribuido() {return nomeUsuarioAtribuido;}
    public void setNomeUsuarioAtribuido(String nomeUsuarioAtribuido) {this.nomeUsuarioAtribuido = nomeUsuarioAtribuido;}

    public Long getIdUsuarioAtribuido() { return idUsuarioAtribuido; }
    public void setIdUsuarioAtribuido(Long idUsuarioAtribuido) { this.idUsuarioAtribuido = idUsuarioAtribuido; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public String getDataRealocacao() { return dataRealocacao; }
    public void setDataRealocacao(String dataRealocacao) { this.dataRealocacao = dataRealocacao; }
}
