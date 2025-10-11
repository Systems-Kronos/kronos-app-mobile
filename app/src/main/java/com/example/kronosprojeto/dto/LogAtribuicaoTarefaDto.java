package com.example.kronosprojeto.dto;

public class LogAtribuicaoTarefaDto {
    private Long id;
    private Long idTarefa;
    private Long idUsuarioAtribuido;
    private String observacao;
    private String dataRealocacao; // formato yyyy-MM-dd

    public LogAtribuicaoTarefaDto(Long idTarefa, Long idUsuarioAtribuido, String observacao, String dataRealocacao) {
        this.idTarefa = idTarefa;
        this.idUsuarioAtribuido = idUsuarioAtribuido;
        this.observacao = observacao;
        this.dataRealocacao = dataRealocacao;
    }


    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdTarefa() { return idTarefa; }
    public void setIdTarefa(Long idTarefa) { this.idTarefa = idTarefa; }

    public Long getIdUsuarioAtribuido() { return idUsuarioAtribuido; }
    public void setIdUsuarioAtribuido(Long idUsuarioAtribuido) { this.idUsuarioAtribuido = idUsuarioAtribuido; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public String getDataRealocacao() { return dataRealocacao; }
    public void setDataRealocacao(String dataRealocacao) { this.dataRealocacao = dataRealocacao; }
}
