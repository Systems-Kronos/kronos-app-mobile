package com.example.kronosprojeto.service;
import com.example.kronosprojeto.dto.LoginRequestDto;
import com.example.kronosprojeto.model.Token;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {

    @POST("/api/usuario/login")
    Call<Token> login(@Body LoginRequestDto loginRequest);
}