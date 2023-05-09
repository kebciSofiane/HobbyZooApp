package com.example.hobbyzooapp.Sessions;

import java.sql.Time;
import java.util.Calendar;
public class Session {

    private String name;
    private Time time;
    private int month, day, year;


    public Session(String name, Time time, int day, int month , int year) {
        this.name = name;
        this.time = time;
        this.month = month;
        this.day = day;
        this.year = year;

    }
    public String getName() {return name;}

    public Time getTime() {return time;}

    public int getMonth() {return month;}

    public int getDay() {return day;}

    public int getYear() {return year;}
}