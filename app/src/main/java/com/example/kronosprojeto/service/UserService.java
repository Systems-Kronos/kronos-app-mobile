package com.example.kronosprojeto.service;

import com.example.kronosprojeto.dto.SenhaDto;
import com.example.kronosprojeto.dto.UserResponseDto;


import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;
import retrofit2.http.Path;


public interface UserService {
    @GET("/api/usuario/selecionarCpf/{cpf}")
    Call<UserResponseDto> getUserByCPF(@Header("Authorization") String token, @Path("cpf") String cpf);
    @PUT("/api/usuario/atualizar/{id}")
    Call<String> updateUser(@Header("Authorization") String token, @Body Map<String, Object>campos, @Path("id") String id);

    @GET("/api/usuario/selecionarNoSec/{cpf}")
    Call<UserResponseDto> getTelefoneByCpf(@Path("cpf") String cpf);

    @PUT("/api/usuario/atualizarSenha/{id}")
    Call<Void> updatePassword(
            @Path("id") String id,
            @Body SenhaDto senha
    );




}

