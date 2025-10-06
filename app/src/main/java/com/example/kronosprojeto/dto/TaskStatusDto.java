package com.example.kronosprojeto.dto;

import com.google.gson.annotations.SerializedName;

public class TaskStatusDto {

    @SerializedName("id")
    private long id;

    @SerializedName("status")
    private String status;

    public TaskStatusDto() {}

    public TaskStatusDto(long id, String status) {
        this.id = id;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
