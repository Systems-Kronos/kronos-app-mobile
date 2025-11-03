package com.example.kronosprojeto.service;
import com.example.kronosprojeto.dto.ChatBotResponseDto;
import com.example.kronosprojeto.model.ChatBotSession;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ChatBotService {

    @GET("new_session")
    Call<ChatBotSession> createNewSession(
            @Query("user_id") String usuarioId
    );

    @GET("chat")
    Call<ChatBotResponseDto> sendMessage(
            @Query("query") String query,
            @Query("session_id") String sessionId
    );
}
