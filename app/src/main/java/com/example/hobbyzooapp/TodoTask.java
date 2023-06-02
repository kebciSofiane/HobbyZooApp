package com.example.hobbyzooapp;

public class TodoTask {
    String taskname;
    Boolean completed;

    public TodoTask(String task, Boolean done){
        this.taskname =task;
        this.completed =done;
    }

    public void setTaskname(String taskname) {
        this.taskname = taskname;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public String getTaskname() {
        return taskname;
    }

    public Boolean isCompleted() {
        return completed;
    }
}