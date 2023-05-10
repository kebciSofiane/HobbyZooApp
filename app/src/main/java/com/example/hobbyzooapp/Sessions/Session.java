package com.example.hobbyzooapp.Sessions;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Session {

    private String name;
    private Time time;
    private int month, day, year;


    public Session(String name, Time time, int day, int month , int year) {
        this.name = name;
        this.time = time;
        this.day = day;
        this.month = month;
        this.year = year;
    }


    public String getName() {return name;}

    public Time getTime() {return time;}

    public int getMonth() {return month;}

    public int getDay() {return day;}

    public int getYear() {return year;}
}