package com.example.kronosprojeto.dto;

import com.google.gson.annotations.SerializedName;

public class ChatBotResponseDto {
    @SerializedName("resposta")
    private String response;

    public String getResponse() { return response; }
    public void setResponse(String resposta) { this.response = resposta; }
}

