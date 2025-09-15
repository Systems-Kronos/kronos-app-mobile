package com.example.kronosprojeto.service;

import com.example.kronosprojeto.dto.UserResponseDto;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;


public interface UserService {

    @GET("/api/usuario/selecionarCpf/{cpf}")
    Call<UserResponseDto> getUserByCPF(@Header("Authorization") String token, @Path("cpf") String cpf);

}

