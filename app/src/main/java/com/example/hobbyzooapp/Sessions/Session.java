package com.example.hobbyzooapp.Sessions;

import java.sql.Time;

public class Session {

    private String sessionId,activityId,activityName, image, done;
    private Time time;
    private int month, day, year;
    String pet;

    public Session(String id, String activityId, String activityName, Time time,
                   int day, int month , int year, String image, String pet, String done) {
        this.sessionId = id;
        this.activityId = activityId;
        this.activityName = activityName;
        this.time = time;
        this.day = day;
        this.month = month;
        this.year = year;
        this.image = image;
        this.pet = pet;
        this.done = done;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getActivityId() {
        return activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public Time getTime() {return time;}

    public int getDay() {return day;}

    public int getMonth() {return month;}

    public int getYear() {return year;}

    public String getImage() {return image;}

    public String getPet() {return pet;}

    public String getDone() {return done;}}