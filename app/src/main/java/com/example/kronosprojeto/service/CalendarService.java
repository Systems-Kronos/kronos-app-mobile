package com.example.kronosprojeto.service;

import com.example.kronosprojeto.model.Calendar;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CalendarService {

    @GET("api/calendario/selecionar/usuario/{idUsuario}")
    Call<List<Calendar>> searchUser(@Path("idUsuario") String idUsuario);
    @POST("api/calendario/adicionar")
    Call<Calendar> insertReport(@Body Calendar calendario);

    @PUT("api/calendario/atualizar/{id}")
    Call<Calendar> updateReport(@Path("id") String id, @Body Calendar calendario);

    @DELETE("api/calendario/deletar/{id}")
    Call<Void> deleteReport(@Path("id") String id);
}
