package com.example.kronosprojeto.dto;

public class UserResponseDto {

    private long id;
    private UserResponseDto gestor;
    private String nome;
    private boolean booleanGestor;
    private String cpf;
    private String telefone;
    private String email;
    private String senha;
    private String foto;
    private boolean ativo;
    private CompanyResponseDto empresa;
    private SectorResponseDto setor;

    public CompanyResponseDto getEmpresa() {
        return empresa;
    }

    public void setEmpresa(CompanyResponseDto empresa) {
        this.empresa = empresa;
    }

    public SectorResponseDto getSetor() {
        return setor;
    }

    public void setSetor(SectorResponseDto setor) {
        this.setor = setor;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UserResponseDto getGestor() {
        return gestor;
    }

    public void setGestor(UserResponseDto gestor) {
        this.gestor = gestor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isBooleanGestor() {
        return booleanGestor;
    }

    public void setBooleanGestor(boolean booleanGestor) {
        this.booleanGestor = booleanGestor;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public UserResponseDto(long id, UserResponseDto gestor, String nome, boolean booleanGestor, String cpf, String telefone, String email, String senha, String foto, boolean ativo, CompanyResponseDto empresa, SectorResponseDto setor) {
        this.id = id;
        this.gestor = gestor;
        this.nome = nome;
        this.booleanGestor = booleanGestor;
        this.cpf = cpf;
        this.telefone = telefone;
        this.email = email;
        this.senha = senha;
        this.foto = foto;
        this.ativo = ativo;
        this.empresa = empresa;
        this.setor = setor;
    }

    public UserResponseDto() {
    }
}
