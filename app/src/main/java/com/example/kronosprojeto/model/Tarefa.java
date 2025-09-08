package com.example.kronosprojeto.model;

import java.util.Date;

public class Tarefa {
    protected String title;
    protected Date day;
    protected int priority;
    protected String sector;

    protected Date dateTerm;

    String tag;

    public Tarefa(String title, Date day, int priority, String sector, String tag, Date dateTerm) {
        this.title = title;
        this.day = day;
        this.priority = priority;
        this.sector = sector;
        this.tag = tag;
        this.dateTerm = dateTerm;
    }

    public int getPriority() {
        return priority;
    }

    public Date getDay() {
        return day;
    }

    public String getTag() {
        return tag;
    }

    public String getSector() {
        return sector;
    }

    public String getTitle() {
        return title;
    }

    public void setDateTerm(Date day) {
        this.dateTerm = day;
    }

    public void setPrioridade(int priority) {
        this.priority = priority;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public Date getDateTerm(){
        return dateTerm;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setTitle(String titulo) {
        this.title = titulo;
    }
}
