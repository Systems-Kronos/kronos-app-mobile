package com.example.kronosprojeto.service;

import com.example.kronosprojeto.dto.TaskDetailsDto;
import com.example.kronosprojeto.model.Notification;
import com.example.kronosprojeto.model.Task;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NotificationService {
    @GET("/api/notificacoes/selecionar/{idUsuario}")
    Call<List<Notification>> getNotificationsByUserID(@Path("idUsuario")long idUsuario);


}
