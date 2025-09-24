package com.example.kronosprojeto.service;

import com.example.kronosprojeto.model.Calendar;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CalenderService {
    @GET("selecionar/usuario/{idUsuario}")
    Call<List<Calendar>> buscarPorUsuario(@Path("idUsuario") Integer idUsuario);

}
