package com.example.kronosprojeto.ui.Chat;

public class ChatMessage {
    private String text;
    private boolean fromUser;

    public ChatMessage(String text, boolean fromUser) {
        this.text = text;
        this.fromUser = fromUser;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public boolean isFromUser() {
        return fromUser;
    }
}
