package com.example.kronosprojeto.model;

import java.util.Date;

public class Task {
    protected String title;
    protected Date day;
    protected int priority;
    protected String sector;

    protected Date dateTerm;

    String tag;

    public Task(String title, Date day, int priority, String sector, String tag, Date dateTerm) {
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

    public String getSector() {
        return sector;
    }

    public String getTitle() {
        return title;
    }

    public Date getDateTerm(){
        return dateTerm;
    }

}
