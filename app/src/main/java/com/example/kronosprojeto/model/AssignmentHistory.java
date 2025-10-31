package com.example.kronosprojeto.model;


import com.google.gson.annotations.SerializedName;

public class AssignmentHistory {

    @SerializedName("titulo")
    private String title;

    @SerializedName("descricao")
    private String description;

    @SerializedName("nomeUsuarioAtribuido")
    private String assignedUserName;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAssignedUserName() {
        return assignedUserName;
    }

    public AssignmentHistory(String title, String description, String assignedUserName) {
        this.title = title;
        this.description = description;
        this.assignedUserName = assignedUserName;
    }
}
