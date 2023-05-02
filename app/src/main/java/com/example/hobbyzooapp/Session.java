package com.example.hobbyzooapp;

import java.sql.Time;

public class Session {

    private String name;
    private Time time;
    private int month, day;

    public Session(String name, Time time, int day, int month) {
        this.name = name;
        this.time = time;
        this.month = month;
        this.day = day;
    }
}
