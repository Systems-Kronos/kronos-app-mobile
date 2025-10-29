package com.example.kronosprojeto.model;

import com.google.gson.annotations.SerializedName;

public class ChatBotSession {

    @SerializedName("session_id")
    private String session_id;

    public String getSession_id() {
        return session_id;
    }
}
