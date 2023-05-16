package com.example.hobbyzooapp.Category;
import android.graphics.Color;

import com.example.hobbyzooapp.Activities.Activity;

import java.util.ArrayList;

public class Category {
    private String id;
    private String name;
    private String color;
    private ArrayList<Activity> activities;

    public ArrayList<Activity> getActivities() {
        return activities;
    }

    public Category(String id, String name, String color, ArrayList<Activity> activities) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.activities=activities;

    }
    public String getName() {return name;}

    public String getId() {
        return id;
    }

    public String getColor() {
        return color;
    }
}