package com.example.kronosprojeto.dto;


public class LoginAdmRequestDTO {

    private String email;

    private String senha;

    public LoginAdmRequestDTO(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }
}
