package com.example.kronosprojeto.service;

import com.example.kronosprojeto.dto.LoginAdmRequestDTO;
import com.example.kronosprojeto.dto.LoginAdmResponseDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AdministratorService {
    @POST("/api/administracao/loginArea")
    Call<LoginAdmResponseDTO> login(@Body LoginAdmRequestDTO loginRequest);

}
