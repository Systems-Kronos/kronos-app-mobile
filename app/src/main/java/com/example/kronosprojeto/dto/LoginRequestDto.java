package com.example.kronosprojeto.dto;

public class LoginRequestDto {
    private String cpf;
    private String senha;

    public String getCpf() {
        return cpf;
    }

    public void seCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getSenha() {
        return senha;
    }

    public void setPassword(String password) {
        this.senha = senha;
    }

    public LoginRequestDto(String cpf, String senha) {
        this.cpf = cpf;
        this.senha = senha;
    }
}
