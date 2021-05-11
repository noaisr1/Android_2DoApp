package com.example.taskslistfinalproject;

import java.util.Calendar;

/**
 * TaskModel class that represents the data model for SQL database.
 *
 * @author Noa Israeli
 */

public class TaskModel {
    private int id;
    private String name;
    private String desc;
    private Calendar date;
    private int urgency;
    private boolean isDone;

    public TaskModel(int id, String name, String desc, Calendar date, int urgency, boolean isDone) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.date = date;
        this.urgency = urgency;
        this.isDone = isDone;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public int getUrgency() {
        return urgency;
    }

    public void setUrgency(int urgency) {
        this.urgency = urgency;
    }

    public boolean isDone() {
        return isDone;
    }

    public int intIsDone(){
        if(isDone) return 1 ;
        else return 0;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
