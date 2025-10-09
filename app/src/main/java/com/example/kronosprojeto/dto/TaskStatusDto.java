package com.example.kronosprojeto.dto;

import com.google.gson.annotations.SerializedName;

public class TaskStatusDto {

    private String status;

    public TaskStatusDto(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}




