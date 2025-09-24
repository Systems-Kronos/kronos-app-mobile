package com.example.kronosprojeto.service;


import com.example.kronosprojeto.dto.TaskDetailsDto;
import com.example.kronosprojeto.model.Calendar;
import com.example.kronosprojeto.model.Task;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TaskService {
    @GET("/api/tarefa/selecionarFunction/{idUsuario}")
    Call<List<Task>> getTasksByUserID(@Path("idUsuario")long idUsuario,
                                      @Header("Authorization") String token,
                                      @Query("tipoTarefa") String tipoTarefa,
                                      @Query("status") String status);

    @GET("/api/tarefa/selecionar/{idTarefa}")
    Call<TaskDetailsDto> getTaskById(@Path("idTarefa")long idTarefa,
                                     @Header("Authorization") String token
    );

}
