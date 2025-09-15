package com.example.kronosprojeto.dto;

public class SectorResponseDto {

    private long id;
    private String nome;


    public float getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }


    public void setId( long id ) {
        this.id = id;
    }

    public void setNome( String nome ) {
        this.nome = nome;
    }

    public SectorResponseDto() {
    }

    public SectorResponseDto(long id, String nome) {
        this.id = id;
        this.nome = nome;
    }
}
