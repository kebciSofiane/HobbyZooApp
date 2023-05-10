package com.example.hobbyzooapp.Sessions;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Session {

    private String name;
    private Time time;
    private int month, day, year;
    private LocalDate localDate;


    public Session(String name, Time time, int day, int month , int year,LocalDate localDate) {
        this.name = name;
        this.time = time;
        this.month = month;
        this.day = day;
        this.year = year;
        this.localDate=localDate;
    }


    public static ArrayList<Session> eventsList = new ArrayList<>();

    public static ArrayList<Session> eventsForDate(LocalDate date)
    {
        ArrayList<Session> events = new ArrayList<>();

        for(Session event : eventsList)
        {
            if(event.getDate().equals(date))
                events.add(event);
        }

        return events;
    }

    private LocalDate getDate() { return this.localDate;}

    public String getName() {return name;}

    public Time getTime() {return time;}



    public int getMonth() {return month;}

    public int getDay() {return day;}

    public int getYear() {return year;}
}