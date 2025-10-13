package com.example.kronosprojeto.service;


import com.example.kronosprojeto.dto.LogAtribuicaoTarefaDto;
import com.example.kronosprojeto.dto.TaskDetailsDto;
import com.example.kronosprojeto.dto.TaskStatusDto;
import com.example.kronosprojeto.model.Calendar;
import com.example.kronosprojeto.model.Task;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TaskService {
    @GET("/api/tarefa/selecionarFunction/{idUsuario}")
    Call<List<Task>> getTasksByUserID(@Path("idUsuario")long idUsuario,
                                      @Header("Authorization") String token,
                                      @Query("tipoTarefa") String tipoTarefa,
                                      @Query("status") String status);

    @POST("/api/log-atribuicao/adicionar")
    Call<LogAtribuicaoTarefaDto> adicionarLog(
            @Header("Authorization") String token,
            @Body LogAtribuicaoTarefaDto dto
    );

    @GET("/api/tarefa/selecionar/{idTarefa}")
    Call<TaskDetailsDto> getTaskById(@Path("idTarefa")long idTarefa,
                                     @Header("Authorization") String token
    );

    @PUT("/api/tarefa/atualizar")
    Call<String> updateTask(@Body TaskStatusDto tarefa, @Header("Authorization") String token);

    @PUT("/api/tarefa/atualizarStatus/{id}")
    Call<String> updateStatus(
            @Header("Authorization") String token,
            @Path("id") Long id,
            @Body TaskStatusDto status
    );

    @GET("/api/log-atribuicao/selecionarTarefa/{id}")
    Call<LogAtribuicaoTarefaDto> searchLogByTask(
            @Path("id")long idTarefa
    );


}
