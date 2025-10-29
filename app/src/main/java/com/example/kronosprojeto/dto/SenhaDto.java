package com.example.kronosprojeto.dto;

import androidx.annotation.Size;

import com.google.gson.annotations.SerializedName;

public class SenhaDto {

    private String novaSenha;

    public SenhaDto(String newPassword){
        this.novaSenha = newPassword;
    }

    public String getNovaSenha(){
        return novaSenha;
    }

}

