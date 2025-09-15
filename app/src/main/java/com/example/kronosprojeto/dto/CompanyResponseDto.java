package com.example.kronosprojeto.dto;

public class CompanyResponseDto {
    private long id;
    private String nome;
    private String cnpj;
    private String email;
    private String telefone;
    private String cep;


    // Getter Methods

    public float getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getCep() {
        return cep;
    }

    // Setter Methods

    public void setId( long id ) {
        this.id = id;
    }

    public void setNome( String nome ) {
        this.nome = nome;
    }

    public void setCnpj( String cnpj ) {
        this.cnpj = cnpj;
    }

    public void setEmail( String email ) {
        this.email = email;
    }

    public void setTelefone( String telefone ) {
        this.telefone = telefone;
    }

    public void setCep( String cep ) {
        this.cep = cep;
    }

    public CompanyResponseDto(long id, String nome, String cnpj, String email, String telefone, String cep) {
        this.id = id;
        this.nome = nome;
        this.cnpj = cnpj;
        this.email = email;
        this.telefone = telefone;
        this.cep = cep;
    }

    public CompanyResponseDto() {
    }
}
